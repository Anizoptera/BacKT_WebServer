package azadev.backt.webserver.utils

import java.text.SimpleDateFormat
import java.util.*


// http://stackoverflow.com/a/8642463/4899346
private val httpDateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).apply {
	timeZone = TimeZone.getTimeZone("UTC")
}

fun dateToHttpFormat(date: Date) = httpDateFormat.format(date)!!

fun parseHttpDate(str: String): Date? {
	return try { httpDateFormat.parse(str)!! }
	catch(e: Throwable) { null }
}
