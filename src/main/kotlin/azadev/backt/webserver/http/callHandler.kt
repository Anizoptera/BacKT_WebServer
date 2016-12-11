package azadev.backt.webserver.http


fun callHandler(f: CallReferences.() -> Boolean) = f
