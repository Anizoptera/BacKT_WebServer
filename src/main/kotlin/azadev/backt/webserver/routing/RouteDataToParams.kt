package azadev.backt.webserver.routing


class RouteDataToParams(
		val routeData: RouteData,
		val params: RouteParams
) {
	operator fun component1() = routeData
	operator fun component2() = params
}
