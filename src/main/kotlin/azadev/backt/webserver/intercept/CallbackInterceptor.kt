package azadev.backt.webserver.intercept

import azadev.backt.webserver.http.CallReferences


class CallbackInterceptor(
		val callback: CallReferences.()->Boolean
) : AInterceptor
{
	override fun CallReferences.intercept() = callback()
}
