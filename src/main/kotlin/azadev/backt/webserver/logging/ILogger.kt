package azadev.backt.webserver.logging


interface ILogger
{
	fun v(msg: String?, ex: Throwable?)
	fun d(msg: String?, ex: Throwable?)
	fun i(msg: String?, ex: Throwable?)
	fun w(msg: String?, ex: Throwable?)
	fun e(msg: String?, ex: Throwable?)
}
