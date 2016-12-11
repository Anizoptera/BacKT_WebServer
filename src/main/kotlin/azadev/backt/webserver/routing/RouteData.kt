package azadev.backt.webserver.routing

import azadev.backt.webserver.intercept.AInterceptor
import azadev.backt.webserver.intercept.InterceptOn
import io.netty.handler.codec.http.HttpMethod


class RouteData(
		val url: azadev.backt.webserver.routing.Route,
		val method: HttpMethod?,
		val interceptor: AInterceptor,
		val interceptOn: InterceptOn
) {
	override fun toString() = "RouteData($method; $url; ${interceptor.javaClass.simpleName}; $interceptOn)"
}
