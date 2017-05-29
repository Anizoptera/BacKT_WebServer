package azadev.backt.webserver.intercept

import azadev.backt.webserver.callref.CallReferences


class CallbackInterceptor(
		val callback: CallReferences.()->Boolean
) : Interceptor
{
	override fun CallReferences.intercept() = callback()
}
