package azadev.backt.webserver.utils


private val REX_HTML_TAGS = "</?[a-z][^<]*?>".toRegex(RegexOption.IGNORE_CASE)
fun String.removeHtmlTags() = replace(REX_HTML_TAGS, "")


private val REX_HTML_BR = "<br/?>".toRegex(RegexOption.IGNORE_CASE)
fun String.replaceBrWithNl() = replace(REX_HTML_BR, "\n")


private val REX_WS = "\\s+".toRegex()
fun String.splitByWhitespaces() = split(REX_WS)
fun String.collapseWs(collapseTo: String = " ") = replace(REX_WS, collapseTo)


fun String.splitBySentences(): List<String> {
	val res = ArrayList<String>()
	val sb = StringBuilder()

	val len = length
	var i = -1
	var foundSentEnd = false
	while (++i < len) {
		val char = get(i)
		when (char) {
			'.', '!', '?', '…' -> {
				foundSentEnd = true
				sb.append(char)
			}
			else -> when {
				char.isWhitespace() -> if (sb.isNotEmpty()) {
					if (foundSentEnd) {
						res.add(sb.toString())
						sb.setLength(0)
						foundSentEnd = false
					}
					else sb.append(char)
				}
				char.isLetterOrDigit() -> {
					foundSentEnd = false
					sb.append(char)
				}
				else -> sb.append(char)
			}
		}
		if (i == len-1 && sb.isNotEmpty())
			res.add(sb.toString())
	}

	return res
}
