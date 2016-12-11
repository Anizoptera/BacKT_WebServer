package azadev.backt.webserver.routing

import java.util.*


class RouteParams
{
	private val params = HashMap<String, String>(1)

	val size: Int
		get() = params.size


	fun getString(name: String) = params[name]

	fun getInt(name: String): Int? {
		return try { getString(name)?.toInt() }
		catch(e: Throwable) { null }
	}


	operator fun get(name: String) = getString(name)

	operator fun set(name: String, value: String) {
		params[name] = value
	}


	override fun toString() = params.toString()


	companion object
	{
		val EMPTY = RouteParams()
	}
}
