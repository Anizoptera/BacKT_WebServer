package azadev.backt.webserver.callref


fun callHandler(f: CallReferences.() -> Boolean) = f
