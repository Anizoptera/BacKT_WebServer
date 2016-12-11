package azadev.backt.webserver.routing

import azadev.backt.webserver.http.CallReferences
import azadev.backt.webserver.http.Request
import java.util.*


fun routeHandler(f: CallReferences.() -> Boolean) = f


fun filterRoutes(routeDatas: List<RouteData>, request: Request): List<RouteDataToParams> {
	val list = ArrayList<RouteDataToParams>(2)

	for (route in routeDatas) {
		if (route.method != null && route.method != request.method)
			continue

		if (route.url.isAny) {
			list.add(RouteDataToParams(route, RouteParams.EMPTY))
			continue
		}

		val params = route.url.parseUri(request.path)
		if (params != null)
			list.add(RouteDataToParams(route, params))
	}

	return list
}
