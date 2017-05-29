package azadev.backt.webserver.routing

import azadev.backt.webserver.intercept.InterceptOn
import azadev.backt.webserver.intercept.Interceptor
import io.netty.handler.codec.http.HttpMethod


class RouteData(
		val url: Route,
		val method: HttpMethod?,
		val interceptor: Interceptor,
		val interceptOn: InterceptOn
) {
	override fun toString() = "RouteData($method; $url; ${interceptor.javaClass.simpleName}; $interceptOn)"
}
