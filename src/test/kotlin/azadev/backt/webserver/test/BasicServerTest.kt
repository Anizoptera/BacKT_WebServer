package azadev.backt.webserver.test

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test


class BasicServerTest : AServerTest()
{
	@Test fun basic() {
		server.get("/", { caseComplete(1) })
		server.get("/first", { caseComplete(2) })
		server.get("/first/second", { caseComplete(3) })
		server.post("/first/second", {
			assertEquals(1, request.wwwFormBody.size)
			assertEquals("111", request.wwwFormBody["a"])
			caseComplete(4)
		})

		get("/", 1)
		get("/first", 2)
		get("/first/second", 3)
		get("/first/second/third", "")
		post("/first/second", "a=111", 4)
	}

	@Test fun routeParams() {
		server.get("/:part1", {
			assertEquals("first", routeParams["part1"])
			caseComplete(1)
		})
		server.get("/first/:part2", {
			assertEquals("second", routeParams["part2"])
			caseComplete(2)
		})
		server.post("/:part3/:part4/:part5/last", {
			assertEquals("first", routeParams["part3"])
			assertEquals("second", routeParams["part4"])
			assertEquals("third", routeParams["part5"])
			caseComplete(3)
		})

		get("/first", 1)
		get("/first/second", 2)
		post("/first/second/third/last", "", 3)
		post("/first/second/third/anotherlast", "", "")
	}

	@Test fun intRouteParams() {
		server.get("/first/:part1", {
			assertEquals("second", routeParams["part1"])
			assertEquals(null, routeParams.getInt("part1"))
			caseComplete(1)
		})
		server.get("/first/#part2", {
			fail()
			caseComplete(2)
		})

		get("/first/second", 1)



		server.get("/third/:part1", {
			assertEquals("123", routeParams["part1"])
			assertEquals(123, routeParams.getInt("part1"))
			caseComplete(3)
		})
		server.get("/third/#part2", {
			assertEquals("123", routeParams["part2"])
			assertEquals(123, routeParams.getInt("part2"))
			caseComplete(4)
		})

		get("/third/123", "3 4")
	}
}
