package azadev.backt.webserver.intercept

import azadev.backt.webserver.WebServer
import azadev.backt.webserver.callref.CallReferences
import azadev.backt.webserver.http.Request
import azadev.backt.webserver.http.Response
import azadev.backt.webserver.routing.RouteParams
import azadev.logging.Logging


interface Interceptor : Logging
{
	fun intercept(server: WebServer, request: Request, response: Response, routeParams: RouteParams)
			= CallReferences(request, response, routeParams).intercept()

	fun CallReferences.intercept() = true
}
