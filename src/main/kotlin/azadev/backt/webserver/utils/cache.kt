package azadev.backt.webserver.utils

import azadev.backt.webserver.http.CallReferences
import java.io.File
import java.util.concurrent.ConcurrentHashMap


// ETag format: ([wW]/)?"([^"]|\\")*"
fun geterateETag(value: String, isWeak: Boolean = true)
		= "${if (isWeak) "W/" else ""}\"$value\""

var eTagCache: ConcurrentHashMap<String, String>? = null
const val MUTEX: Byte = 1

inline fun handleETagCache(callReferences: CallReferences, eTagCacheKey: String? = null, eTagCreator: ()->String): Boolean {
	val incomingETag = callReferences.request.headers["If-None-Match"]

	var eTag: String
	if (eTagCacheKey != null) {
		val cache = eTagCache ?: synchronized(MUTEX) {
			eTagCache = eTagCache ?: ConcurrentHashMap<String, String>(10)
			eTagCache!!
		}

		eTag = cache[eTagCacheKey] ?: ""
		if (eTag.isNullOrEmpty()) {
			eTag = geterateETag(eTagCreator())
			cache[eTagCacheKey] = eTag
			callReferences.logVerbose { "ETag cached: $eTag" }
		}
	}
	else eTag = geterateETag(eTagCreator())

	if (incomingETag != null && eTag.equals(incomingETag, ignoreCase = true)) {
		callReferences.logVerbose { "Asset is not modified (ETag) // eTag=$eTag" }
		return true
	}

	callReferences.response.headers["ETag"] = eTag
	return false
}

fun handleETagCache(callReferences: CallReferences, file: File)
		= handleETagCache(callReferences, file.path, { file.readText().hashCode().toString() })


fun handleLastModCache(callReferences: CallReferences, file: File): Boolean {
	val ifModifiedSince = parseHttpDate(callReferences.request.headers["If-Modified-Since"] ?: "")?.run { (time / 1000L).toInt() }

	val lastModified = (file.lastModified() / 1000L).toInt()
	if (ifModifiedSince == lastModified) {
		callReferences.logVerbose { "Asset is not modified (Last-Modified)" }
		return true
	}

	callReferences.response.headers["Last-Modified"] = lastModified
	return false
}
