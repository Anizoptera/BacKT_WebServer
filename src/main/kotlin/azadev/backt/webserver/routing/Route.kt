package azadev.backt.webserver.routing

import azadev.backt.webserver.utils.isLetterOrDigitASCII
import azadev.backt.webserver.utils.toNotEmptyString
import java.net.URLEncoder


class Route(
		val pattern: String,

		/*
		Sometimes it is very useful to create a Route with domain/scheme predefined.
		You can pass such Route-instance into any class that needs it, and the class
		will be able to create an URL-string without a need to know the domain/scheme.
		 */
		val domain: String = "",
		val scheme: String = SCHEME_HTTP
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


	operator fun invoke(
			vararg params: Any,
			domain: String? = null,
			addDomain: Boolean = domain != null,
			scheme: String? = null,
			addScheme: Boolean = scheme != null,
			queryParams: Map<*, *>? = null
	): String {
		val sb = StringBuilder()

		if (addScheme)
			sb.append(scheme ?: this.scheme)

		if (addDomain || addScheme)
			sb.append(domain ?: this.domain)

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


	operator fun plus(path: String) = Route("$pattern$path", domain, scheme)


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

		const val SCHEME_SAME = "//"
		const val SCHEME_HTTP = "http://"
		const val SCHEME_HTTPS = "https://"
		const val SCHEME_FTP = "ftp://"
		const val SCHEME_FTPS = "ftps://"
	}
}
