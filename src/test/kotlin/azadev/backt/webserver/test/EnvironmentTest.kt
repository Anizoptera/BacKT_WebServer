package azadev.backt.webserver.test

import org.junit.Assert
import org.junit.Test


class EnvironmentTest
{
	@Test fun kotlinTest() {
		Assert.assertEquals(222, testingRun())

		Assert.assertEquals("",         "hello".take(0))
		Assert.assertEquals("he",       "hello".take(2))
		Assert.assertEquals("hello",    "hello".take(20))
		Assert.assertEquals("hello",    "hello".drop(0))
		Assert.assertEquals("llo",      "hello".drop(2))
		Assert.assertEquals("",         "hello".drop(20))
	}

	fun testingRun(): Int {
		111.run { return 222 }
		return 333
	}
}
