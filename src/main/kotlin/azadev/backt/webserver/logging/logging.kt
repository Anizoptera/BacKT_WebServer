package azadev.backt.webserver.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level


private fun Any?.log(level: Level, _msg: Any?, ex: Throwable? = null, args: Array<out Any>? = null) {
	val logger = when (this) {
		is ILoggerHolder -> logger
		is Logger -> this
		is String -> LoggerFactory.getLogger(this)
		else -> LoggerFactory.getLogger(this?.javaClass?.name ?: "null")
	}

	val msg = _msg as? String ?: _msg.toString()

	when (level) {
		Level.TRACE -> when {
			ex != null -> logger.trace(msg, ex)
			args != null -> logger.trace(msg, *args)
			else -> logger.trace(msg)
		}
		Level.DEBUG -> when {
			ex != null -> logger.debug(msg, ex)
			args != null -> logger.debug(msg, *args)
			else -> logger.debug(msg)
		}
		Level.INFO -> when {
			ex != null -> logger.info(msg, ex)
			args != null -> logger.info(msg, *args)
			else -> logger.info(msg)
		}
		Level.WARN -> when {
			ex != null -> logger.warn(msg, ex)
			args != null -> logger.warn(msg, *args)
			else -> logger.warn(msg)
		}
		Level.ERROR -> when {
			ex != null -> logger.error(msg, ex)
			args != null -> logger.error(msg, *args)
			else -> logger.error(msg)
		}
	}
}


fun Any?.logVerbose(msg: Any?) = log(Level.TRACE, msg)
fun Any?.logVerbose(msg: Any?, vararg args: Any) = log(Level.TRACE, msg, null, args)
fun Any?.logVerbose(msg: Any?, ex: Throwable) = log(Level.TRACE, msg, ex)

fun Any?.logDebug(msg: Any?) = log(Level.DEBUG, msg)
fun Any?.logDebug(msg: Any?, vararg args: Any) = log(Level.DEBUG, msg, null, args)
fun Any?.logDebug(msg: Any?, ex: Throwable) = log(Level.DEBUG, msg, ex)

fun Any?.logInfo(msg: Any?) = log(Level.INFO, msg)
fun Any?.logInfo(msg: Any?, vararg args: Any) = log(Level.INFO, msg, null, args)
fun Any?.logInfo(msg: Any?, ex: Throwable) = log(Level.INFO, msg, ex)

fun Any?.logWarning(msg: Any?) = log(Level.WARN, msg)
fun Any?.logWarning(msg: Any?, vararg args: Any) = log(Level.WARN, msg, null, args)
fun Any?.logWarning(msg: Any?, ex: Throwable) = log(Level.WARN, msg, ex)

fun Any?.logError(msg: Any?) = log(Level.ERROR, msg)
fun Any?.logError(msg: Any?, vararg args: Any) = log(Level.ERROR, msg, null, args)
fun Any?.logError(msg: Any?, ex: Throwable) = log(Level.ERROR, msg, ex)


interface ILogging
{
	fun logVerbose(msg: Any?) = log(Level.TRACE, msg)
	fun logVerbose(msg: Any?, vararg args: Any) = log(Level.TRACE, msg, null, args)
	fun logVerbose(msg: Any?, ex: Throwable) = log(Level.TRACE, msg, ex)

	fun logDebug(msg: Any?) = log(Level.DEBUG, msg)
	fun logDebug(msg: Any?, vararg args: Any) = log(Level.DEBUG, msg, null, args)
	fun logDebug(msg: Any?, ex: Throwable) = log(Level.DEBUG, msg, ex)

	fun logInfo(msg: Any?) = log(Level.INFO, msg)
	fun logInfo(msg: Any?, vararg args: Any) = log(Level.INFO, msg, null, args)
	fun logInfo(msg: Any?, ex: Throwable) = log(Level.INFO, msg, ex)

	fun logWarning(msg: Any?) = log(Level.WARN, msg)
	fun logWarning(msg: Any?, vararg args: Any) = log(Level.WARN, msg, null, args)
	fun logWarning(msg: Any?, ex: Throwable) = log(Level.WARN, msg, ex)

	fun logError(msg: Any?) = log(Level.ERROR, msg)
	fun logError(msg: Any?, vararg args: Any) = log(Level.ERROR, msg, null, args)
	fun logError(msg: Any?, ex: Throwable) = log(Level.ERROR, msg, ex)
}
