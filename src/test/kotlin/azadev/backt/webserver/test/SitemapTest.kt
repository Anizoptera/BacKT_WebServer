package azadev.backt.webserver.test

import azadev.backt.webserver.sitemap.Sitemap
import org.junit.*
import org.junit.Assert.*
import java.util.*


class SitemapTest
{
	@Test fun basics() {
		checkContent("", Sitemap())

		checkContent(
				"<url><loc>host1</loc><lastmod>2017-02-11</lastmod><changefreq>weekly</changefreq><priority>1</priority></url>",
				Sitemap().url("host1", "2017-02-11", Sitemap.WEEKLY, 1f)
		)

		val cal = GregorianCalendar()
		checkContent(
"""<url><loc>host1</loc><lastmod>${Sitemap.formatDatetime(cal.time)}</lastmod><changefreq>monthly</changefreq><priority>0.7</priority></url>
<url><loc>host2</loc><lastmod>${Sitemap.formatDatetime(cal.time)}</lastmod><changefreq>daily</changefreq><priority>0.1234</priority></url>
<url><loc>host3</loc><lastmod>${Sitemap.formatDatetime(cal.time, short = true)}</lastmod></url>""".replace("\n", ""),
				Sitemap {
					url {
						loc("host1")
						lastmod = cal
						changefreq(Sitemap.MONTHLY)
						priority(.7f)
					}
					url {
						loc = "host2"
						lastmod(cal.timeInMillis)
						changefreq = Sitemap.DAILY
						priority = .1234f
					}
					url {
						loc = "host3"
						lastmod = Sitemap.formatDatetime(cal.time, short = true)
					}
				}
		)
	}

	private fun checkContent(content: String, sitemap: Sitemap) {
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">$content</urlset>", sitemap.toString())
	}
}
