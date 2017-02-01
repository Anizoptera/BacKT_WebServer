package azadev.backt.webserver.test

import azadev.backt.webserver.utils.*
import org.junit.*
import org.junit.Assert.*


class UtilsTest
{
	@Test fun textUtils() {
		// removeHtmlTags
		assertEquals("hey!", "<b>hey!</b>".removeHtmlTags())
		assertEquals("<bhey!", "<bhey!</b>".removeHtmlTags())
		assertEquals("<<<hey!>>>", "<<<<b>hey!</b>>>>".removeHtmlTags())
		assertEquals("hey!", "<span title=\"17:00:33\" class=\"time\">hey!</SPAN>".removeHtmlTags())

		assertEquals("1<2 but 1>0", "1<2 but 1>0".removeHtmlTags())
		assertEquals("a < b but b > c", "a < b but b > c".removeHtmlTags())


		// replaceBrWithNl
		assertEquals("hey!\nhow r u\n???", "hey!<br>how r u<br/>???".replaceBrWithNl())
		assertEquals("hey!<bro\nhow r u", "hey!<bro<br>how r u".replaceBrWithNl())


		// collapseWs
		assertEquals("hey! how r u ???", "hey!  how r u\n\n \n ???".collapseWs())
		assertEquals("hello", "he o".collapseWs("ll"))


		// splitBySentences
		assertEquals(
				listOf("понравится маленьким «привередам», красивая упаковка.","Шоколад – какие ассоциации?","на ваш вкус.","</p><p>является лидером.","Зерна полбы не вызывают брожения.</p>"),
				"понравится маленьким «привередам», красивая упаковка. Шоколад – какие ассоциации? на ваш вкус. </p><p>является лидером. Зерна полбы не вызывают брожения.</p>".splitBySentences()
		)
		assertEquals(
				listOf("<p>Восхитительное удовольствие!","с восхитительным вкусом.","<b>Банан</b> 80% воды, крахмал (7-20%), витамины С, РР.","не уступают цитрусовым.","Спирулина."),
				"<p>Восхитительное удовольствие! \n \nс восхитительным вкусом. \n<b>Банан</b> 80% воды, крахмал (7-20%), витамины С, РР. не уступают цитрусовым.\nСпирулина. ".splitBySentences()
		)
	}

	@Test fun ipAddressConversions() {
		assertEquals(520712856, ipToInt("31.9.114.152"))
		assertEquals(-1062731675, ipToInt("192.168.0.101"))
		assertEquals(-1, ipToInt("255.255.255.255"))

		assertEquals("31.9.114.152", intToIpString(520712856))
		assertEquals("192.168.0.101", intToIpString(-1062731675))
		assertEquals("255.255.255.255", intToIpString(-1))
	}
}
