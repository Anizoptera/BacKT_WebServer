package azadev.backt.webserver.utils


internal fun <T : CharSequence> T?.notNullOrEmpty(): T? = if (!isNullOrEmpty()) this else null
internal fun <T : CharSequence> T?.notNullOrBlank(): T? = if (!isNullOrBlank()) this else null

internal fun <T : CharSequence> T?.notEmptyElse(default: T): T = if (this != null && this.isNotEmpty()) this else default


internal fun Any?.toTrimmedString(): String = this?.toString()?.trim() ?: ""
internal fun Any?.toNotEmptyString(): String? = this?.toString()?.notNullOrEmpty()
internal fun Any?.toNotEmptyTrimmedString(): String? = this?.toString()?.trim()?.notNullOrEmpty()


internal fun <T : CharSequence> T?.ifNotNullOrEmpty(fn: T.()->Unit): T? {
	if (this != null && this.isNotEmpty()) fn(this)
	return this
}


internal fun Char.isLetterOrDigitASCII()
		= this >= '0' && this <= '9' || this >= 'A' && this <= 'Z' || this >= 'a' && this <= 'z'
