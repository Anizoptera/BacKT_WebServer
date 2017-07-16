package azadev.backt.webserver.utils


private val REX_HTML_TAGS = "</?[a-z][^<]*?>".toRegex(RegexOption.IGNORE_CASE)
fun String.removeHtmlTags() = replace(REX_HTML_TAGS, "")


private val REX_HTML_BR = "<br/?>".toRegex(RegexOption.IGNORE_CASE)
fun String.replaceBrWithNl() = replace(REX_HTML_BR, "\n")


private val REX_WS = "[\\s\\p{Z}]+".toRegex()
fun String.splitByWhitespaces() = split(REX_WS)
fun String.collapseWs(collapseTo: String = " ") = replace(REX_WS, collapseTo)
