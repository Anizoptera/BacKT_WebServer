package azadev.backt.webserver

import azadev.backt.webserver.http.CallReferences
import azadev.backt.webserver.http.HttpRequestHandler
import azadev.backt.webserver.intercept.*
import azadev.backt.webserver.routing.Route
import azadev.backt.webserver.routing.RouteData
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.*


/*
Example Netty web server:
http://netty.io/4.0/xref/io/netty/example/http/helloworld/package-summary.html
 */
class WebServer(
		val port: Int = 80,
		val maxConnections: Int = 1000,
		val exceptionHandler: (CallReferences.(Throwable, InterceptOn)->Unit)? = null
) {
	lateinit var bossGroup: NioEventLoopGroup
	lateinit var workerGroup: NioEventLoopGroup

	val routes = java.util.ArrayList<RouteData>()


	fun start(wait: Boolean = false) {
		bossGroup = NioEventLoopGroup()
		workerGroup = NioEventLoopGroup()

		val bootstrap = ServerBootstrap()
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel::class.java)
				.childHandler(object : ChannelInitializer<SocketChannel>() {
					override fun initChannel(ch: SocketChannel) {
						ch.pipeline().addLast(
//								TraceHandler(),

//								HttpServerCodec(), // request decoder + response encoder
								HttpRequestDecoder(),
								HttpResponseEncoder(),

								// We don't want to handle chunked messages
								HttpObjectAggregator(1048576),

								HttpRequestHandler(this@WebServer)
						)
					}
				})
				.option(ChannelOption.SO_BACKLOG, maxConnections)

		val future = bootstrap.bind(port).sync()

		if (wait)
			future.channel().closeFuture().sync()
	}

	fun stop() {
		// Shutdown all event loops
		bossGroup.shutdownGracefully()
		workerGroup.shutdownGracefully()

		// Wait till all threads are terminated
		bossGroup.terminationFuture().sync()
		workerGroup.terminationFuture().sync()
	}


	fun get(url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(HttpMethod.GET, url, interceptor, interceptOn)
	fun get(url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = get(url, CallbackInterceptor(callback), interceptOn)
	fun get(path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = get(Route(path), interceptor, interceptOn)
	fun get(path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = get(Route(path), CallbackInterceptor(callback), interceptOn)

	fun post(url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(HttpMethod.POST, url, interceptor, interceptOn)
	fun post(url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = post(url, CallbackInterceptor(callback), interceptOn)
	fun post(path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = post(Route(path), interceptor, interceptOn)
	fun post(path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = post(Route(path), CallbackInterceptor(callback), interceptOn)

	fun put(url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(HttpMethod.PUT, url, interceptor, interceptOn)
	fun put(url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = put(url, CallbackInterceptor(callback), interceptOn)
	fun put(path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = put(Route(path), interceptor, interceptOn)
	fun put(path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = put(Route(path), CallbackInterceptor(callback), interceptOn)

	fun head(url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(HttpMethod.HEAD, url, interceptor, interceptOn)
	fun head(url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = head(url, CallbackInterceptor(callback), interceptOn)
	fun head(path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = head(Route(path), interceptor, interceptOn)
	fun head(path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = head(Route(path), CallbackInterceptor(callback), interceptOn)

	fun delete(url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(HttpMethod.DELETE, url, interceptor, interceptOn)
	fun delete(url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = delete(url, CallbackInterceptor(callback), interceptOn)
	fun delete(path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = delete(Route(path), interceptor, interceptOn)
	fun delete(path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = delete(Route(path), CallbackInterceptor(callback), interceptOn)

	fun options(url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(HttpMethod.OPTIONS, url, interceptor, interceptOn)
	fun options(url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = options(url, CallbackInterceptor(callback), interceptOn)
	fun options(path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = options(Route(path), interceptor, interceptOn)
	fun options(path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = options(Route(path), CallbackInterceptor(callback), interceptOn)

	fun patch(url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(HttpMethod.PATCH, url, interceptor, interceptOn)
	fun patch(url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = patch(url, CallbackInterceptor(callback), interceptOn)
	fun patch(path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = patch(Route(path), interceptor, interceptOn)
	fun patch(path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = patch(Route(path), CallbackInterceptor(callback), interceptOn)


	fun getAndPost(url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) {
		get(url, interceptor, interceptOn)
		post(url, interceptor, interceptOn)
	}
	fun getAndPost(url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = getAndPost(url, CallbackInterceptor(callback), interceptOn)
	fun getAndPost(path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = getAndPost(Route(path), interceptor, interceptOn)
	fun getAndPost(path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = getAndPost(Route(path), CallbackInterceptor(callback), interceptOn)


	fun route(method: HttpMethod?, url: Route, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) {
		routes.add(RouteData(url, method, interceptor, interceptOn))
	}
	fun route(method: HttpMethod?, url: Route, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(method, url, CallbackInterceptor(callback), interceptOn)
	fun route(method: HttpMethod?, path: String, interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(method, Route(path), interceptor, interceptOn)
	fun route(method: HttpMethod?, path: String, callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(method, Route(path), CallbackInterceptor(callback), interceptOn)
	fun route(interceptor: AInterceptor, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(null, Route.ANY, interceptor, interceptOn)
	fun route(callback: CallReferences.()->Boolean, interceptOn: InterceptOn = InterceptOn.EXECUTION) = route(null, Route.ANY, CallbackInterceptor(callback), interceptOn)
}
