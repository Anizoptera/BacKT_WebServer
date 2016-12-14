package azadev.backt.webserver.test

import azadev.backt.webserver.WebServer
import org.junit.Test


class ManualTest
{
	@Test fun test() {
		val server = WebServer()

			server.getAndPost("/", {
				println(request.uri)
				true
			})

			server.start(wait = true)
	}
}
