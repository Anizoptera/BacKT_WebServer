package azadev.backt.webserver.test

import azadev.backt.webserver.WebServer
import azadev.backt.webserver.callref.CallReferences
import okhttp3.*
import org.junit.*


abstract class AServerTest
{
	val port = 33377
	lateinit var server: WebServer


	@Suppress("unused")
	@Before fun startServer() {
		print("Starting server")
		server = WebServer(
				port = port,
				exceptionHandler = { ex, _ -> println(ex) }
		)
		server.start()
		println(" OK")
	}

	@Suppress("unused")
	@After fun resetServer() {
		print("Stopping server")
		server.stop()
		println(" OK")
	}


	private fun makeRequest(path: String, method: String, sendData: String? = null, expectedCases: Any? = null): Response {
		val request = Request.Builder()
				.url("http://127.0.0.1:$port$path")

		if (method == POST && sendData != null)
			request.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), sendData))

		val response = OkHttpClient().newCall(request.build()).execute()

		if (expectedCases != null)
			Assert.assertEquals("Expected case IDs: $expectedCases", expectedCases.toString(), response.body().string())

		return response
	}

	fun get(path: String, expectedCases: Any? = null) = makeRequest(path, GET, expectedCases = expectedCases)
	fun post(path: String, sendData: String, expectedCases: Any? = null) = makeRequest(path, POST, sendData = sendData, expectedCases = expectedCases)


	fun CallReferences.caseComplete(caseId: Int): Boolean {
		val dataToSend = response.dataToSend?.toString()
		response.dataToSend = if (dataToSend == null) caseId.toString() else "$dataToSend $caseId"
		return true
	}
}

const val GET = "GET"
const val POST = "POST"
