package azadev.backt.webserver.intercept

import azadev.backt.webserver.http.*
import azadev.backt.webserver.routing.RouteParams


interface AInterceptor
{
	fun intercept(request: Request, response: Response, routeParams: RouteParams)
			= CallReferences(request, response, routeParams).intercept()

	fun CallReferences.intercept() = true
}
