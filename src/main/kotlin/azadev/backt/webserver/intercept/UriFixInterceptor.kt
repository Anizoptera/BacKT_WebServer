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

		val origUrl = "$host$path"


		if (host != null)
			when (wwwMode) {
				WWWMODE_DEL_WWW -> if (host[3] == '.' && host.startsWith("www.", ignoreCase = true)) {
					host = host.drop(4)
				}
				WWWMODE_ADD_WWW -> if (host[3] != '.' && !host.startsWith("www.", ignoreCase = true)) {
					host = "www." + host
				}
			}

		if (pathTrimEndChars != null)
			path = path.trimEnd(*pathTrimEndChars)

		path = path.replace("//+".toRegex(), "/")


		val resUrl = "$host$path"
		if (resUrl == origUrl)
			return true

		trackEvent("Url is fixed", "$origUrl -> $resUrl")
		return response.redirect(resUrl, HttpResponseStatus.MOVED_PERMANENTLY)
	}


	companion object
	{
		const val WWWMODE_NONE: Byte = 1
		const val WWWMODE_ADD_WWW: Byte = 2
		const val WWWMODE_DEL_WWW: Byte = 3
	}
}
