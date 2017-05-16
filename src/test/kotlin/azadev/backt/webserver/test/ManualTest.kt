package azadev.backt.webserver.test

import azadev.backt.webserver.WebServer
import org.junit.*


class ManualTest
{
	@Test fun test() {
		val server = WebServer()

			server.getOrPost("/", {
				println(request.uri)
				true
			})

			server.start(wait = true)
	}
}
