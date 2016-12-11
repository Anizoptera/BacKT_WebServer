package azadev.backt.webserver.logging


interface ILogging
{
	val logger: ILogger?
	val logLevel: LogLevel


	fun logVerbose(ex: Throwable? = null, fmsg: (()->String)? = null) {
		if (logLevel <= LogLevel.VERBOSE)
			logger?.v(fmsg?.invoke(), ex)
	}
	fun logDebug(ex: Throwable? = null, fmsg: (()->String)? = null) {
		if (logLevel <= LogLevel.DEBUG)
			logger?.d(fmsg?.invoke(), ex)
	}
	fun logInfo(ex: Throwable? = null, fmsg: (()->String)? = null) {
		if (logLevel <= LogLevel.INFO)
			logger?.i(fmsg?.invoke(), ex)
	}
	fun logWarning(ex: Throwable? = null, fmsg: (()->String)? = null) {
		if (logLevel <= LogLevel.WARNING)
			logger?.w(fmsg?.invoke(), ex)
	}
	fun logError(ex: Throwable? = null, fmsg: (()->String)? = null) {
		if (logLevel <= LogLevel.ERROR)
			logger?.e(fmsg?.invoke(), ex)
	}
}
