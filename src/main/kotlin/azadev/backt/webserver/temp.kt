package azadev.backt.webserver


fun trackEvent(category: Any, action: Any? = null, label: Any? = null, value: Number? = null)
		= println("[EVENT] $category; $action; $label; $value")
