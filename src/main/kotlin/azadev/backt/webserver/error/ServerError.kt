package azadev.backt.webserver.error


interface ServerError
{
	val type: CharSequence
	val code: CharSequence

	val summary: String get() = "${javaClass.simpleName}($type;$code)"


	companion object
	{
		fun create(type: CharSequence, code: CharSequence) = object : ServerError {
			override val type = type
			override val code = code
		}
	}
}
