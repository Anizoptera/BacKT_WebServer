package azadev.backt.webserver.utils


// Simplicity is awesome. Taken from here:
// http://www.regular-expressions.info/email.html
val REX_EMAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$".toRegex(RegexOption.IGNORE_CASE)

val String.isEmailValid: Boolean
	get() = REX_EMAIL.matches(this)
