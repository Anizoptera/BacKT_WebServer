package azadev.backt.webserver.http

import azadev.backt.webserver.WebServer
import azadev.backt.webserver.intercept.InterceptOn
import azadev.backt.webserver.routing.RouteDataToParams
import azadev.backt.webserver.routing.filterRoutes
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.handler.codec.http.*
import java.io.File


class HttpRequestHandler(
		val server: WebServer
) : ChannelInboundHandlerAdapter()
{
	override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
		msg as? FullHttpRequest ?: return

		val request = Request(msg)
		val response = Response()
		val routes = filterRoutes(server.routes, request)


		try {
			if (!runRoutes(InterceptOn.PRE_REQUEST, routes, request, response))
				return writeResponse(ctx, request, response, routes)
		}
		catch(e: Throwable) {
			server.logError(e) { "Exception during ${InterceptOn.PRE_REQUEST} stage" }
			response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
			return writeResponse(ctx, request, response, routes)
		}


		// Looking for routes with a specific path (not ANY).
		// If there are no routings with a specific path, then assume that it is 404.
		if (routes.find { !it.routeData.url.isAny } != null)
			try {
				runRoutes(InterceptOn.PRE_EXECUTION, routes, request, response)
				&& runRoutes(InterceptOn.EXECUTION, routes, request, response)
				&& runRoutes(InterceptOn.POST_EXECUTION, routes, request, response)
			}
			catch(e: Throwable) {
				server.logError(e) { "Exception during routing invocation" }
				response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
			}
		else {
			// TODO: Return an allowed-methods response (405)
			response.setStatus(HttpResponseStatus.NOT_FOUND)
		}

		writeResponse(ctx, request, response, routes)
	}

	private fun runRoutes(interceptOn: InterceptOn, routes: List<RouteDataToParams>, request: Request, response: Response): Boolean {
		for ((route, params) in routes)
			if (route.interceptOn === interceptOn && !route.interceptor.intercept(request, response, params))
				return false

		return true
	}

	private fun writeResponse(ctx: ChannelHandlerContext, request: Request, response: Response, routes: List<RouteDataToParams>) {
		if (response.status.code() / 100 == 4 || response.status.code() / 100 == 5)
			runRoutes(InterceptOn.ERROR, routes, request, response)


		if (response.sendFilePath != null) {
			// http://netty.io/4.1/xref/io/netty/example/http/file/HttpStaticFileServerHandler.html

			val file = File(response.sendFilePath)
			val fileLength = file.length()

			val httpResponse = DefaultHttpResponse(HttpVersion.HTTP_1_1, response.status)
			httpResponse.headers().add(response.headers)
			ctx.write(httpResponse)

			ctx.writeAndFlush(DefaultFileRegion(file, 0, fileLength), ctx.newProgressivePromise())
					.addListener(ChannelFutureListener.CLOSE)
		}
		else {
			val sendBuffer = response.sendBuffer
			val buf = when (sendBuffer) {
				null -> Unpooled.buffer(0)
				else -> Unpooled.copiedBuffer(sendBuffer.toString(), Charsets.UTF_8)
			}

			val httpResponse = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf)
			httpResponse.headers().add(response.headers)
			ctx.writeAndFlush(httpResponse)
					.addListener(ChannelFutureListener.CLOSE)
		}

		runRoutes(InterceptOn.POST_REQUEST, routes, request, response)
	}
}
