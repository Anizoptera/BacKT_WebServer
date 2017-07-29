@file:Suppress("unused")

package azadev.backt.webserver.http

import azadev.backt.webserver.error.ServerError
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.cookie.DefaultCookie
import io.netty.handler.codec.http.cookie.ServerCookieEncoder
import java.io.File
import javax.activation.MimetypesFileTypeMap


class Response
{
	var status = HttpResponseStatus.OK
	val statusNotFound: Boolean get() = setStatus(HttpResponseStatus.NOT_FOUND)
	val statusForbidden: Boolean get() = setStatus(HttpResponseStatus.FORBIDDEN)
	val statusBadRequest: Boolean get() = setStatus(HttpResponseStatus.BAD_REQUEST)
	val statusServerError: Boolean get() = setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
	val statusNotModified: Boolean get() = setStatus(HttpResponseStatus.NOT_MODIFIED)

	val headers = DefaultHttpHeaders()

	var dataToSend: Any? = null
	var filePathToSend: String? = null
	val hasContent: Boolean
		get() = dataToSend != null || filePathToSend != null

	var errors: ArrayList<ServerError>? = null
	val firstError: ServerError?
		get() = errors?.firstOrNull()


	fun send(obj: Any?, contentType: String? = null): Boolean {
		dataToSend = obj
		if (contentType != null)
			headers["Content-Type"] = contentType
		return false
	}
	fun send(obj: Any?, statusCode: HttpResponseStatus, contentType: String? = null): Boolean {
		setStatus(statusCode)
		send(obj, contentType)
		return false
	}

	fun sendFile(file: File, contentType: String? = null, autoContentType: Boolean = false): Boolean {
		filePathToSend = file.path

		if (contentType != null)
			headers["Content-Type"] = contentType
		else if (autoContentType) {
			headers["Content-Type"] = when {
				file.extension.equals("css", ignoreCase = true) -> "text/css"
				file.extension.equals("js", ignoreCase = true) -> "application/javascript"
				else -> MimetypesFileTypeMap().getContentType(file)!! // default is "application/octet-stream"
			}
		}

		return false
	}
	fun sendFile(pathname: String, contentType: String? = null, autoContentType: Boolean = false)
			= sendFile(File(pathname), contentType, autoContentType)


	fun setStatus(_status: HttpResponseStatus): Boolean {
		status = _status
		return false
	}

	fun redirect(url: String, redirectType: HttpResponseStatus = HttpResponseStatus.FOUND): Boolean {
		setStatus(redirectType)
		headers["Location"] = url
		return false
	}

	fun addError(code: ServerError): Boolean {
		if (errors == null)
			errors = ArrayList<ServerError>(1)
		errors?.add(code)
		return true
	}

	fun setCookie(
			name: String,
			value: String,
			maxAgeDays: Int? = null,
			domain: String? = null,
			path: String = "/",
			isHttpOnly: Boolean = true,
			isSecure: Boolean = false
	): Boolean {
		val cookie = DefaultCookie(name, value)

		// 0 - remove the cookie immediately
		// null - remove after the browser is closed
		if (maxAgeDays != null)
			cookie.setMaxAge(maxAgeDays * 60*60*24L)
		else
			cookie.setMaxAge(Long.MIN_VALUE)

		cookie.setDomain(domain)
		cookie.setPath(path)

		cookie.isHttpOnly = isHttpOnly
		cookie.isSecure = isSecure

		headers["Set-Cookie"] = ServerCookieEncoder.STRICT.encode(cookie)
		return false
	}
}
