package azadev.backt.webserver.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory


interface ILogging
{
	val logger: Logger

	fun getDefaultLogger() = LoggerFactory.getLogger(javaClass)!!
}
