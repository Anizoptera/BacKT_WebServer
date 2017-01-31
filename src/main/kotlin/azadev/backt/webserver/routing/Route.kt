package azadev.backt.webserver.routing

import azadev.backt.webserver.utils.isLetterOrDigitASCII
import azadev.backt.webserver.utils.toNotEmptyString
import java.net.URLEncoder


class Route(
		val pattern: String,
		val domain: String = ""
) {
	val wildcards = arrayListOf<String>()

	val partCount = pattern.trim('/').count { it == '/' }

	val regex = run {
		var starCount = 0
		val pt = pattern
				.trim('/')
				// Escaping symbols that are mutual for url-path and regex
				// (except STAR-*, it's used as the wildcard)
				.replace("[().+$]".toRegex(), "\\\\$0")
				.replace("\\*|(?:[:#](\\w+))".toRegex()) {
					when (it.value[0]) {
						'*' -> {
							wildcards.add("*".padEnd(++starCount, '*'))
							"(.*?)"
						}
						':' -> {
							wildcards.add(it.groupValues[1])
							"([\\w\\-.~!$&'()*+,;=:@%]+?)"
						}
						'#' -> {
							wildcards.add(it.groupValues[1])
							"(\\d+)"
						}
						else -> throw RuntimeException("Unexpected first char: $it")
					}
				}
		Regex("^$pt$")
	}

	val isAny = pattern == "*"


	operator fun invoke(vararg params: Any, addDomain: Boolean = false, addHttp: Boolean = false, queryParams: Map<*, *>? = null): String {
		val sb = StringBuilder()

		if (addHttp)
			sb.append("http://")

		if (addDomain || addHttp)
			sb.append(domain)

		val lastIdx = pattern.length-1
		var isInWildcard = false
		var idx = 0

		for (i in 0..lastIdx) {
			val c = pattern[i]
			if (c == '*' || c == ':' || c == '#') {
				isInWildcard = c != '*'
				sb.append(params.getOrNull(idx++) ?: "")
			}
			else {
				if (!c.isLetterOrDigitASCII())
					isInWildcard = false

				if (!isInWildcard)
					sb.append(c)
			}
		}

		val query = queryParams
		if (query != null && query.isNotEmpty()) {
			val startLen = sb.length
			for ((_key, values) in query) {
				val key = _key?.toNotEmptyString() ?: continue

				if (values is List<*>)
					for (v in values)
						appendQuery(sb, startLen, key, v)
				else
					appendQuery(sb, startLen, key, values)
			}
		}

		return sb.toString()
	}

	private fun appendQuery(sb: StringBuilder, startLen: Int, key: String, _value: Any?) {
		if (sb.length == startLen) sb.append('?')
		else sb.append('&')

		sb.append(URLEncoder.encode(key, "UTF-8"))

		val value = _value?.toNotEmptyString() ?: return
		sb.append('=')
		sb.append(URLEncoder.encode(value, "UTF-8"))
	}


	operator fun plus(path: String) = Route("$pattern$path")


	fun parseUri(path: String): RouteParams? {
		if (isAny)
			return RouteParams.EMPTY

		val cleanPath = path.trim('/').replace("/+".toRegex(), "/")
		if (!wildcards.contains("*") && cleanPath.count { it == '/' } != partCount)
			return null

		val iterator = regex.findAll(cleanPath).iterator()
		if (!iterator.hasNext())
			return null

		val groups = iterator.next().groupValues
		val params = RouteParams()

		if (groups.size-1 != wildcards.size)
			throw RuntimeException("Wildcard count doesn't match to regex group count // pattern=$pattern / path=$path / wildcards=$wildcards / groups=$groups / regex=$regex")

		for (i in 1..groups.size-1)
			params[wildcards[i-1]] = groups[i]

		return params
	}


	companion object
	{
		val ANY = Route("*")
		val ROOT = Route("/")
	}
}
