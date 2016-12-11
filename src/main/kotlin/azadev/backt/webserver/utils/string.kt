package azadev.backt.webserver.utils


fun <T : CharSequence> T?.notNullOrEmpty(): T? = if (!isNullOrEmpty()) this else null
fun <T : CharSequence> T?.notNullOrBlank(): T? = if (!isNullOrBlank()) this else null

fun <T : CharSequence> T?.notEmptyElse(default: T): T = if (this != null && this.isNotEmpty()) this else default


fun Any?.toTrimmedString(): String = this?.toString()?.trim() ?: ""
fun Any?.toNotEmptyString(): String? = this?.toString()?.notNullOrEmpty()
fun Any?.toNotEmptyTrimmedString(): String? = this?.toString()?.trim()?.notNullOrEmpty()


fun Char.isLetterOrDigitASCII()
		= this >= '0' && this <= '9' || this >= 'A' && this <= 'Z' || this >= 'a' && this <= 'z'
