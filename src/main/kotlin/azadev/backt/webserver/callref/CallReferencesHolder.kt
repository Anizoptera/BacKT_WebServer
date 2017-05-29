package azadev.backt.webserver.callref

import azadev.backt.webserver.http.Request
import azadev.backt.webserver.http.Response
import azadev.backt.webserver.routing.RouteParams
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.cookie.Cookie


/**
 * Sometimes you need to create a class that holds [CallReferences]
 * and intensively uses its properties. In this case you can easily inherit your class
 * from this interface, and get many useful stuff from the [CallReferences] object.
 * The only thing you have to do is to override the [callReferences] property.
 */
interface CallReferencesHolder
{
	val callReferences: CallReferences

	val request: Request get() = callReferences.request
	val response: Response get() = callReferences.response
	val routeParams: RouteParams get() = callReferences.routeParams

	val method: HttpMethod get() = request.method
	val isGET: Boolean get() = method == HttpMethod.GET
	val isPOST: Boolean get() = method == HttpMethod.POST
	val isPUT: Boolean get() = method == HttpMethod.PUT
	val isPATCH: Boolean get() = method == HttpMethod.PATCH
	val isDELETE: Boolean get() = method == HttpMethod.DELETE

	val queryParams: Map<String, List<String>> get() = request.queryParams
	val bodyValues: Map<String, Any> get() = request.bodyValues

	val cookies: Map<String, Cookie> get() = request.cookies
}
