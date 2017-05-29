package azadev.backt.webserver.utils

import azadev.backt.webserver.callref.CallReferences
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap


// ETag format: ([wW]/)?"([^"]|\\")*"
fun geterateETag(value: String, isWeak: Boolean = true)
		= "${if (isWeak) "W/" else ""}\"$value\""

var eTagCache: ConcurrentHashMap<String, String>? = null
const val MUTEX: Byte = 1

inline fun handleETagCache(callReferences: CallReferences, cacheKey: String? = null, tagCreator: ()->String): Boolean {
	val incomingETag = callReferences.request.headers["If-None-Match"]

	var eTag: String
	if (cacheKey != null) {
		val cache = eTagCache ?: synchronized(MUTEX) {
			eTagCache = eTagCache ?: ConcurrentHashMap<String, String>(10)
			eTagCache!!
		}

		eTag = cache[cacheKey] ?: ""
		if (eTag.isNullOrEmpty()) {
			eTag = geterateETag(tagCreator())
			cache[cacheKey] = eTag
		}
	}
	else eTag = geterateETag(tagCreator())

	if (incomingETag != null && eTag.equals(incomingETag, ignoreCase = true))
		return true // File is not modified

	callReferences.response.headers["ETag"] = eTag
	return false
}

fun handleETagCache(callReferences: CallReferences, file: File): Boolean {
	val lastMod = file.lastModified()
	return handleETagCache(callReferences, "${file.path}|$lastMod", { lastMod.hashCode().toString() })
}


fun handleLastModCache(callReferences: CallReferences, file: File): Boolean {
	val ifModifiedSince = parseHttpDate(callReferences.request.headers["If-Modified-Since"] ?: "")?.run { (time / 1000L).toInt() }

	val lastModifiedMsec = file.lastModified()
	val lastModified = (lastModifiedMsec / 1000L).toInt()
	if (ifModifiedSince == lastModified)
		return true // File is not modified

	callReferences.response.headers["Last-Modified"] = dateToHttpFormat(Date(lastModifiedMsec))
	return false
}
