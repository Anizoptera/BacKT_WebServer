package azadev.backt.webserver.error


interface ServerError
{
	val type: String
	val code: String

	val details: Any? get() = null

	val summary: String get() = makeSummary(detailed = false)
	val detailedSummary: String get() = makeSummary(detailed = true)


	operator fun invoke(details: Any?) = create(type, code, details)

	infix fun eq(other: ServerError) = type == other.type && code == other.code


	private fun makeSummary(detailed: Boolean): String {
		val sb = StringBuilder()

		sb.append(javaClass.simpleName)
		sb.append('(').append(type).append(';').append(code)

		if (details != null)
			sb.append(";{has details}")

		sb.append(')')

		if (detailed && details != null)
			sb.append('\n').append(details)

		return sb.toString()
	}


	companion object
	{
		fun create(type: String, code: String) = object : ServerError {
			override val type get() = type
			override val code get() = code
		}

		fun create(type: String, code: String, details: Any?) = object : ServerError {
			override val type get() = type
			override val code get() = code
			override val details get() = details
		}
	}
}
