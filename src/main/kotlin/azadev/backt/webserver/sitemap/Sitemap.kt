package azadev.backt.webserver.sitemap

import java.text.SimpleDateFormat
import java.util.*


class Sitemap(
		body: (Sitemap.()->Unit)? = null
) {
	private val sb = StringBuilder(1000)


	init { body?.invoke(this) }


	/**
	 * @param loc
	 * An absolute page URL: http://www.example.com/path
	 *
	 * @param lastmod
	 * See [formatDatetime] for more info.
	 *
	 * @param changefreq
	 * Possible values: always, hourly, daily, weekly, monthly, yearly, never.
	 * Each value has a corresponding constant like [Sitemap.MONTHLY]
	 *
	 * @param priority
	 * The priority of this URL relative to other URLs on your site.
	 * Valid values range from 0.0 to 1.0
	 */
	fun url(loc: String, lastmod: Any? = null, changefreq: String? = null, priority: Float? = null): Sitemap {
		val sb = sb

		sb.append("<url>")

		sb.append("<loc>").append(loc).append("</loc>")

		if (lastmod != null)
			sb.append("<lastmod>").append(formatDatetime(lastmod)).append("</lastmod>")

		if (changefreq != null)
			sb.append("<changefreq>").append(changefreq).append("</changefreq>")

		if (priority != null)
			sb.append("<priority>").append(if (priority >= 1f || priority <= 0f) priority.toInt() else priority).append("</priority>")

		sb.append("</url>")

		return this
	}

	fun url(block: UrlAttribute.()->Unit): Sitemap {
		val url = UrlAttribute()
		url.block()
		return url(url.loc, url.lastmod, url.changefreq, url.priority)
	}


	override fun toString(): String {
		val out = StringBuilder()
		out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")
		out.append(sb)
		out.append("</urlset>")
		return out.toString()
	}


	class UrlAttribute(
			var loc: String = "",
			var lastmod: Any? = null,
			var changefreq: String? = null,
			var priority: Float? = null
	) {
		fun loc(value: String) { loc = value }
		fun lastmod(value: Any) { lastmod = value }
		fun changefreq(value: String) { changefreq = value }
		fun priority(value: Float) { priority = value }
	}


	companion object
	{
		const val ALWAYS    = "always"
		const val HOURLY    = "hourly"
		const val DAILY     = "daily"
		const val WEEKLY    = "weekly"
		const val MONTHLY   = "monthly"
		const val YEARLY    = "yearly"
		const val NEVER     = "never"

		val w3cFullDatetimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
		val w3cShortDatetimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)


		/**
		 * @param datetime
		 * - Either [String] in W3C Datetime format: https://www.w3.org/TR/NOTE-datetime
		 *  -- For example: 2017-02-11T12:34:56+03:00
		 *  -- The time part may be omitted: 2017-02-11
		 * - Or [Int] representing unix time is seconds.
		 * - Or [Long] representing unix time is milliseconds.
		 * - Or [Calendar].
		 * - Or [Date].
		 */
		fun formatDatetime(datetime: Any, short: Boolean = false): String {
			val format = if (short) w3cShortDatetimeFormat else w3cFullDatetimeFormat
			val res = when (datetime) {
				is Int -> format.format(Date(datetime * 1000L))
				is Long -> format.format(Date(datetime))
				is Date -> format.format(datetime)
				is Calendar -> format.format(datetime.time)
				else -> datetime.toString()
			}

			// SimpleDateFormat doesn't add a colon into time-zone: 2017-02-11T12:34:56+0300
			// But W3C requires it.
			if (!short && res.length == 24)
				return res.take(22) + ":" + res.drop(22)

			return res
		}
	}
}
