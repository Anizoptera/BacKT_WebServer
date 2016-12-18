package azadev.backt.webserver.intercept

import azadev.backt.webserver.WebServer
import azadev.backt.webserver.http.*
import azadev.backt.webserver.routing.RouteParams


interface AInterceptor
{
	fun intercept(server: WebServer, request: Request, response: Response, routeParams: RouteParams)
			= CallReferences(request, response, routeParams, server).intercept()

	fun CallReferences.intercept() = true
}
