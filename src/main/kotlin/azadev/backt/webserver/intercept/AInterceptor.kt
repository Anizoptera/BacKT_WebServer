package azadev.backt.webserver.intercept

import azadev.backt.webserver.WebServer
import azadev.backt.webserver.http.*
import azadev.backt.webserver.routing.RouteParams
import azadev.logging.Logging


interface AInterceptor : Logging
{
	fun intercept(server: WebServer, request: Request, response: Response, routeParams: RouteParams)
			= CallReferences(request, response, routeParams).intercept()

	fun CallReferences.intercept() = true
}
