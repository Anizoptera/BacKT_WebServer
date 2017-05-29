package azadev.backt.webserver.callref

import azadev.backt.webserver.http.Request
import azadev.backt.webserver.http.Response
import azadev.backt.webserver.routing.RouteParams


/**
 * Wrapper containing general data needed to handle a request.
 */
class CallReferences(
		override val request: Request,
		override val response: Response,
		override val routeParams: RouteParams
) : CallReferencesHolder
{
	override val callReferences = this
}
