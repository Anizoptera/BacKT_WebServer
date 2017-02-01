package azadev.backt.webserver.utils

import java.net.InetAddress


fun ipToInt(address: ByteArray): Int {
	var res = 0
	for (byte in address)
		res = res shl 8 or (byte.toInt() and 255)
	return res
}
fun ipToInt(address: String)
		= ipToInt(address.split('.').let { ByteArray(4) { i -> it[i].toInt().toByte() } })
fun ipToInt(address: InetAddress)
		= ipToInt(address.address)


fun intToIpString(address: Int): String {
	val sb = StringBuilder()
	for (i in 0..3) {
		sb.append((address shr (8*(3-i))) and 255)
		if (i < 3) sb.append('.')
	}
	return sb.toString()
}
