package azadev.backt.webserver.http

import azadev.backt.webserver.utils.notEmptyElse
import azadev.backt.webserver.utils.toIntSafe
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.cookie.Cookie
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import io.netty.handler.codec.http.multipart.*


class Request(
		val httpRequest: FullHttpRequest
) {
	val method: HttpMethod get() = httpRequest.method()
	val scheme: String get() = "http" // Only HTTP is supported by now
	val uri: String get() = httpRequest.uri()
	var path: String
	var query: String
	var queryParams: Map<String, List<String>>


	val headers: HttpHeaders get() = httpRequest.headers()
	val host: String? get() = headers["Host"]
	val port: Int? get() = host?.substringAfter(':', "")?.toIntSafe(80)
	val referer: String? get() = headers["Referer"]

	private var _cookies: Map<String, Cookie>? = null
	val cookies: Map<String, Cookie>
		get() {
			if (_cookies != null)
				return _cookies!!

			val str = httpRequest.headers()[HttpHeaderNames.COOKIE]
			val map = when {
				str == null || str.isBlank() -> emptyMap()
				else -> ServerCookieDecoder.STRICT.decode(str).associateBy(Cookie::name)
			}

			_cookies = map
			return map
		}


	val body: String get() = bodyBytes.toString(Charsets.UTF_8)
	val bodyBytes: ByteBuf get() = httpRequest.content()

	val bodyValues: Map<String, Any>
		get() = _bodyValues ?: parseMultivaluedBody(httpRequest, HttpPostRequestDecoder(factory, httpRequest)).apply {
			_bodyValues = this
		}

	val wwwFormBody: Map<String, Any>
		get() = _wwwFormBody ?: parseMultivaluedBody(httpRequest, HttpPostStandardRequestDecoder(factory, httpRequest)).apply {
			_wwwFormBody = this
		}
	val multipartBody: Map<String, Any>
		get() = _multipartBody ?: parseMultivaluedBody(httpRequest, HttpPostMultipartRequestDecoder(factory, httpRequest)).apply {
			_multipartBody = this
		}

	private var _bodyValues: Map<String, Any>? = null
	private var _wwwFormBody: Map<String, Any>? = null
	private var _multipartBody: Map<String, Any>? = null


	var associatedData: Any? = null


	init {
		val uri = uri
		val qIndex = uri.indexOf('?')
		path = if (qIndex >= 0) uri.take(qIndex) else uri
		query = if (qIndex >= 0) uri.drop(qIndex+1) else ""
		this.queryParams = QueryStringDecoder(query, false).parameters()
	}


	companion object
	{
		private val factory = DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)


		private fun parseMultivaluedBody(httpRequest: FullHttpRequest, decoder: InterfaceHttpPostRequestDecoder): HashMap<String, Any> {
			val map = HashMap<String, Any>(2)
			decoder.offer(httpRequest)

			loop1@while (true)
				try {
					val data = decoder.next() ?: break@loop1
					val name = data.name
					val lastIndex = name.lastIndex

					// Handling multiple values in the PHP-style:
					// <input type="checkbox" name="checkboxArray[]">
					// <input type="checkbox" name="checkboxArray[key]">
					loop2@while (lastIndex > 1 && name[lastIndex] == ']') {
						val idx = name.indexOf('[')
						if (idx <= 0) break@loop2

						val cleanName = name.take(idx)
						val key = name.substring(idx+1, lastIndex)

						@Suppress("UNCHECKED_CAST")
						val subMap = map[cleanName] as? LinkedHashMap<String, Any>
								?: LinkedHashMap<String, Any>(2).apply { map[cleanName] = this }

						subMap[key.notEmptyElse(subMap.size.toString())] = if (data is Attribute) data.value else data
						continue@loop1
					}

					// We'll get here only if the data has a plain name:
					// <input type="checkbox" name="checkboxname">
					map[name] = if (data is Attribute) data.value else data
				}
				catch(e: HttpPostRequestDecoder.EndOfDataDecoderException) { break }

			return map
		}
	}
}
