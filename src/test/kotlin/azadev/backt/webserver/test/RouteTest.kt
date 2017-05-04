package azadev.backt.webserver.test

import azadev.backt.webserver.routing.Route
import org.junit.*


class RouteTest
{
	@Test fun compilation() {
		checkCompilation("/first/second",           "/first/:p1", "second")
		checkCompilation("/first/second/third",     "/first/:p1/third", "second")
		checkCompilation("/first/",                 "/first/:p1")
		checkCompilation("/item123",                "/item#id", 123)
		checkCompilation("/item123",                "/:type#id", "item", 123)
		checkCompilation("123item",                 "#i:type", 123, "item")
		checkCompilation("/p/123",                  "/p/#id*", 123)
		checkCompilation("/p/123//second",          "/p/#id/:type/second", 123)

		checkCompilation("/assets/main.css",        "/assets/:name.:ext", "main", "css")
		checkCompilation("/assets/main.css",        "/assets/*.:ext", "main", "css")
		checkCompilation("/assets/img/bg.jpg",      "/assets/:type/*.:ext", "img", "bg", "jpg")

		checkCompilation("/img_s1/123_title.jpg",   "/img*/#id*.:ext", "_s1", 123, "_title", "jpg")

		checkCompilation("",                        "")
		checkCompilation("",                        "*")
		checkCompilation("/",                       "/")
		checkCompilation("/",                       "/*")
		checkCompilation("/123",                    "/*", 123)
		checkCompilation("/",                       "/", "second")
	}

	@Test fun compilation_withQuery() {
		checkCompilation("/path?a=1&a=2&b=3",           "/path", query = mapOf("a" to listOf(1,2), "b" to 3))

		checkCompilation("?1=222",                      "", query = mapOf(1 to 222))

		checkCompilation("/",                           "/", query = emptyMap<String, String>())
		checkCompilation("/",                           "/", query = mapOf("a" to emptyList<String>()))
		checkCompilation("/?a",                         "/", query = mapOf("a" to null))
		checkCompilation("/",                           "/", query = mapOf(null to 123))
		checkCompilation("/",                           "/", query = mapOf("" to 123))
		checkCompilation("/?a",                         "/", query = mapOf("a" to ""))

		checkCompilation(
				"/first/second?a=111&b=%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82%21+%D0%BA%D0%B0%D0%BA+%D0%B4%D0%B5%D0%BB%D0%B0%3F",
				"/first/:2nd",
				"second",
				query = mapOf("a" to 111, "b" to "привет! как дела?")
		)
	}

	private fun checkCompilation(expected: String, pattern: String, vararg params: Any, query: Map<*, *>? = null) {
		Assert.assertEquals(expected, (Route(pattern))(*params, queryParams = query))
	}


	@Test fun matching() {
		checkMatching("*",                      "/first/second",            true)
		checkMatching("/",                      "/first/second",            false)
		checkMatching("",                       "/first/second",            false)
		checkMatching("/first",                 "/first/second",            false)
		checkMatching("first/second",           "/first/second",            true)
		checkMatching("/first/second/",         "/first/second",            true)
		checkMatching("/first/second/",         "/first///second",          true)

		checkMatching("/first/:action",         "/first/second",            true,   "action" to "second")
		checkMatching("/first/:a/#id",          "/first/second/123",        true,   "a" to "second", "id" to 123)
		checkMatching("/first/item#id",         "/first/item123",           true,   "id" to 123)
		checkMatching("/first/#id:type",        "/first/123item",           true,   "id" to 123, "type" to "item")
		checkMatching(":type#id/second",        "item123/second",           true,   "type" to "item", "id" to 123)
		checkMatching("/:type#id",              "item123",                  true,   "type" to "item", "id" to 123)
		checkMatching("/js/:filename.js",       "/js/main.js",              true,   "filename" to "main")
		checkMatching("/:type/:name.:ext",      "/img/123.jpg",              true,   "type" to "img", "name" to "123", "ext" to "jpg")

		checkMatching("/first/:action",         "/first/abc-._~!$&'()*+,;=:@%def",  true,   "action" to "abc-._~!$&'()*+,;=:@%def")

		checkMatching("/:type#id",              "item",                     false)
		checkMatching("/first/:a/#id",          "/first/second/",           false)
		checkMatching("/first/:a/#id",          "/first/second/third",      false)

		checkMatching("/assets/:type/*.:ext",   "/assets/img/bg.jpg",       true,   "type" to "img", "*" to "bg", "ext" to "jpg")
		// Questionable case
		checkMatching("/assets/:type/*.:ext",   "/assets/img/bg.top.jpg",   true,   "type" to "img", "*" to "bg", "ext" to "top.jpg")
		checkMatching("/img*/#id*.:ext",        "/img/123.jpg",             true,   "*" to "", "id" to "123", "**" to "", "ext" to "jpg")
		checkMatching("/img*/#id*.:ext",        "/img_s1/123_title.jpg",    true,   "*" to "_s1", "id" to "123", "**" to "_title", "ext" to "jpg")
		checkMatching("/img*/#id*.:ext",        "/img_s1/123_title",        false)
		checkMatching("/img*/#id*.:ext",        "/img_s1",                  false)
		checkMatching("/img*/#id*.:ext",        "/img",                     false)

		checkMatching("/file(2).jpg",           "/file(2).jpg",             true)

		checkMatching("/*",                     "/first/item123",           true,   "*" to "first/item123")
		checkMatching("/first*",                "/first/item123",           true,   "*" to "/item123")
		checkMatching("/first*",                "/first",                   true,   "*" to "")
		checkMatching("/first/*#id",            "/first/item123",           true,   "*" to "item", "id" to 123)
		checkMatching("*/*#id",                 "/first/item123",           true,   "*" to "first", "**" to "item", "id" to 123)
		checkMatching("*/*#id",                 "/first/item",              false)
	}

	fun checkMatching(pattern: String, path: String, matched: Boolean, vararg params: Pair<String, Any>) {
		val actual = Route(pattern).parseUri(path)

		if (!matched)
			return Assert.assertEquals(null, actual)

		Assert.assertEquals(params.size, actual?.size)

		for ((name, value) in params)
			Assert.assertEquals(value, when (value) {
				is Int -> actual?.getInt(name)
				else -> actual?.getString(name)
			})
	}
}
