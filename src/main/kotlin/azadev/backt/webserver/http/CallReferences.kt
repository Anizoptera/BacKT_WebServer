package azadev.backt.webserver.http

import azadev.backt.webserver.routing.RouteParams
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.cookie.Cookie


/**
 * Wrapper containing general data needed to handle a request.
 */
class CallReferences(
		override val request: Request,
		override val response: Response,
		override val routeParams: RouteParams
) : ICallReferencesHolder
{
	override val callReferences = this
}


/**
 * Sometimes you need to create a class that holds [CallReferences]
 * and intensively uses its properties. In this case you can easily inherit your class
 * from this interface, and get many useful stuff from the [CallReferences] object.
 * The only thing you have to do is to override the [callReferences] property.
 */
interface ICallReferencesHolder
{
	val callReferences: CallReferences

	val request: Request get() = callReferences.request
	val response: Response get() = callReferences.response
	val routeParams: RouteParams get() = callReferences.routeParams

	val method: HttpMethod get() = request.method
	val isGET: Boolean get() = method == HttpMethod.GET
	val isPOST: Boolean get() = method == HttpMethod.POST

	val queryParams: Map<String, List<String>> get() = request.queryParams
	val bodyValues: Map<String, Any> get() = request.bodyValues

	val cookies: Map<String, Cookie> get() = request.cookies
}
