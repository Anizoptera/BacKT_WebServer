package azadev.backt.webserver.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory


interface ILoggerHolder
{
	val logger: Logger

	fun getDefaultLogger() = LoggerFactory.getLogger(javaClass)!!
}
