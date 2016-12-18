package azadev.backt.webserver.intercept

import azadev.backt.webserver.http.Request
import azadev.backt.webserver.http.Response
import azadev.backt.webserver.routing.RouteParams
import azadev.backt.webserver.trackEvent
import io.netty.handler.codec.http.HttpResponseStatus


class UriFixInterceptor(
		val pathTrimEndChars: CharArray? = null,
		val wwwMode: Byte = WWWMODE_NONE
) : AInterceptor
{
	override fun intercept(request: Request, response: Response, routeParams: RouteParams): Boolean {
		var host = request.host
		var path = request.path

		if (host != null && host.isNotEmpty())
			when (wwwMode) {
				WWWMODE_DEL_WWW -> if (host[3] == '.' && host.startsWith("www.", ignoreCase = true)) {
					host = host.drop(4)
				}
				WWWMODE_ADD_WWW -> if (host[3] != '.' && !host.startsWith("www.", ignoreCase = true)) {
					host = "www." + host
				}
			}

		if (path.isNotEmpty() && path != "/") {
			if (pathTrimEndChars != null)
				path = path.trimEnd(*pathTrimEndChars)

			path = path.replace("//+".toRegex(), "/")
		}


		if (host == request.host && path == request.path)
			return true // Nothing's changed

		trackEvent("Url is fixed", "${request.host ?: ""}${request.path} -> ${host ?: ""}$path")

		return response.redirect(buildString {
			if (host != null) {
				append(request.scheme).append("://").append(host)
				request.port?.let { append(':').append(it) }
			}
			append(path)
			if (request.query.isNotEmpty())
				append('?').append(request.query)
		}, HttpResponseStatus.MOVED_PERMANENTLY)
	}


	companion object
	{
		const val WWWMODE_NONE: Byte = 1
		const val WWWMODE_ADD_WWW: Byte = 2
		const val WWWMODE_DEL_WWW: Byte = 3
	}
}
