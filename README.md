# BacKT WebServer

A Netty-based web-server serving files and web-pages via the HTTP-protocol and providing an advanced routing system.

**BacKT WebServer** is a part of «**Back-end Kotlin Tool Set**» that consists of:

- [BacKT SQL](https://github.com/Anizoptera/BacKT_SQL) (a library for SQL-databases)
- [BacKT WebServer](https://github.com/Anizoptera/BacKT_WebServer) (this one)

It also uses [KotLog](https://github.com/Anizoptera/Kotlin-Logging-Facade) for logging.

## Installation

```gradle
repositories {
	maven { url "http://dl.bintray.com/azadev/maven" }
}

dependencies {
	compile "azadev.backt:backt_webserver:0.9.1"
}
```

### Hello World

Run this code and open `http://localhost/`:

```kotlin
val server = WebServer()

server.get("/", {
	response.send("Hello World!", "text/plain")
})

server.post("/do-job", {
	// do some job
	response.send("Done!", "text/plain")
})

server.start()
```

Available methods:
- `get`
- `post`
- `getOrPost`
- `put`
- `head`
- `delete`
- `options`
- `patch`

Each method receives:

1. **Pattern** – `String` pattern or `Route` instance;
2. **Interceptor** – either a lambda returning `Boolean`, or an instance of `AInterceptor`;
3. (optional) **Position** of the callback in the execution flow – `InterceptOn` enum.

### 1. Pattern

**BacKT WebServer** provides an advanced routing system, which allows to filter requests using sophisticated patterns and wildcards.

PATTERN | WILL MATCH | WON'T MATCH
------- | ---------- | -----------
\* | (anthing) | (nothing)
/ | / | /user
/user | /user | /<br>/user234<br>/user/john
/user/:name | /user/john | /user<br>/user/john/status
/user/#id | /user/234 | /user<br>/user/john
/user#id/:action | /user234/info<br>/user567/delete | /user234<br>/users/list<br>/user234/info/send
/user\* | /user<br>/user234/info | /page/user234
/files/\*.css | /files/fonts.css<br>/files/css/main/fonts.css | /uploads/fonts.css
/\*/\*#id | /user/id234 | /user<br>/id234<br>/user/john

Pattern may be defined either as a plain `String`, or a `Route` instance:

```kotlin
// Plain String:
server.get("/blog/post#id", { ... })

// Route object:
val BLOG_POST = Route("/blog/post#id")
server.get(BLOG_POST, { ... })
```

`Route` is a wrapper for URL patterns that can help you handle all the URLs within your site:

```kotlin
// Create a Route directly:
val BLOG = Route("/blog")

// Create a new Route based on the previous one:
val BLOG_POST = BLOG + "/post#id"

// Compile to a String:
val string = BLOG_POST(234) // will compile to: /blog/post234

// With query-params:
val string = BLOG(queryParams = mapOf("post" to 234)) // will compile to: /blog?post=234
```

### 2. Interceptor

**Interceptor** is a kind of callback or handler that must _populate_ the `response`. For example:

```kotlin
val server = WebServer()

server.get("/", {
	response.setCookie(name = "uid", value = "123")
	response.send("Hello World!", "text/plain")
})
server.get("/file.js", {
	response.sendFile(file, "text/javascript")
})
server.get("/redirect-me", {
	response["Location"] = url
	response.status = HttpResponseStatus.FOUND
})

server.start()
```

Interceptor may be a lambda, or an instance of `AInterceptor`:

```kotlin
class MyInterceptor : AInterceptor
{
	override fun CallReferences.intercept(): Boolean {
		return response.send("Hello World!")
	}
}
```

Both lambda and the `intercept` method are being called using a `CallReferences` instance as a receiver. This is a kind of a data object that holds the data needed to handle the request.

Interceptors are named thus (not, let's say, "handlers") to be more distinctive: "handler" is a very frequently used name. Netty uses it too.

### 3. Position

Each interceptor has a position within the execution flow. The execution flow consists of 6 steps:

- `PRE_REQUEST`
- `PRE_EXECUTION`, `EXECUTION` and `POST_EXECUTION`
- `ERROR`
- `POST_REQUEST`

By default interceptors are being added to the `EXECUTION` step. To explicitly specify the position of an interceptor use the `InterceptOn` enum:

```kotlin
// This callback will be called first, for any page;
// If it returns "false", then *EXECUTION interceptors will be skipped:
server.get("*", { ... }, InterceptOn.PRE_REQUEST)

// This will be called only if an error happens on /blog* pages:
server.get("/blog*", { ... }, InterceptOn.ERROR)
```

There may be many interceptors within a single step. Interceptors are being called following these rules:
- Interceptors are being called accordingly to the step they are belong to, and in the order they are added.
- If an interceptor returns `false`, then next interceptors within this step won't be called.
- If an interceptor of the `PRE_REQUEST` step returns `false`, the execution flow skips all the `*EXECUTION` steps.
- If an interceptor of any `*EXECUTION` step returns `false`, the execution flow skips all the remaining interceptors within `*EXECUTION` steps. For example, if a callback of the `PRE_EXECUTION` step returns `false`, neither `EXECUTION` nor `POST_EXECUTION` interceptors will be called.
- `ERROR` interceptors will be called before `POST_REQUEST` if `Response` status code is set to `4xx` or `5xx`.
- In case an unhandled exception is caught, the status code will be set to `500` and the execution flow will jump right to the `ERROR` step.

## License

This software is released under the MIT License.
See [LICENSE.md](LICENSE.md) for details.
