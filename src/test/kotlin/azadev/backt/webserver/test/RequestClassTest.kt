package azadev.backt.webserver.test

import azadev.backt.webserver.http.Request
import io.netty.buffer.Unpooled
import io.netty.buffer.UnpooledByteBufAllocator
import io.netty.handler.codec.http.*
import org.junit.Assert.assertEquals
import org.junit.Test


class RequestClassTest
{
	@Test fun queryParamsTest() {
		assertEquals(mapOf("p1" to listOf("1", "11"), "p2" to listOf("2")), request(uri = "/doc?p1=1&p2=2&p1=11").queryParams)
	}

	@Test fun bodyParserTest() {
		assertEquals("hello", request("hello").body)
		assertEquals(Unpooled.wrappedBuffer("hello".toByteArray()), request("hello").bodyBytes)

		assertEquals(hashMapOf("msg" to "hello"), request("msg=hello").wwwFormBody)
		assertEquals(hashMapOf("msg" to "hello"), request("msg=hello").bodyValues)

		assertEquals(hashMapOf("color" to mapOf("0" to "red", "1" to "orange")), request("color[]=red&color[]=orange").wwwFormBody)
		assertEquals(
				hashMapOf("color" to mapOf("1st" to "red", "2nd" to "orange", "2" to "green", "3rd" to "blue")),
				request("color[1st]=red&color[2nd]=orange&color[]=green&color[3rd]=blue").bodyValues
		)

		val multipartData = """------WebKitFormBoundaryAXaisup7awQ3FfpV
Content-Disposition: form-data; name="title"

hello
------WebKitFormBoundaryAXaisup7awQ3FfpV
Content-Disposition: form-data; name="descr"

world
------WebKitFormBoundaryAXaisup7awQ3FfpV--
"""
		val multipartHeader = mapOf("Content-Type" to "multipart/form-data; boundary=----WebKitFormBoundaryAXaisup7awQ3FfpV", "Content-Length" to "238")

		assertEquals(hashMapOf("title" to "hello", "descr" to "world"), request(multipartData, headers = multipartHeader).multipartBody)
		assertEquals(hashMapOf("title" to "hello", "descr" to "world"), request(multipartData, headers = multipartHeader).bodyValues)
	}


	fun request(content: String = "", uri: String = "/", headers: Map<*, *> = emptyMap<Any, Any>()): Request {
		val buf = UnpooledByteBufAllocator(true).buffer(5)
		buf.writeBytes(content.toByteArray())

		val request = DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, buf)

		for ((name, value) in headers)
			request.headers().add(name.toString(), value.toString())

		return Request(request)
	}
}
