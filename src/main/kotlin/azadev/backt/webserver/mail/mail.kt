package azadev.backt.webserver.mail

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


/*
CAUTION!
These helpers are very raw and rude.
They will be refactored in the near future.
 */

// http://www.tutorialspoint.com/java/java_sending_email.htm
// http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
// https://support.google.com/mail/answer/13287
// http://www.sql.ru/forum/1195541/javamail-smtp-i-yandex

fun sendMail(props: Properties, from: String, pass: String, to: String, subject: String, text: String): Boolean {
	val session = Session.getInstance(props, object : Authenticator() {
		override fun getPasswordAuthentication() = PasswordAuthentication(from, pass)
	})

//	session.debug = true

	try {
		// create a message
		val msg = MimeMessage(session)
		msg.setFrom(InternetAddress(from))
		msg.setRecipients(Message.RecipientType.TO, to)
		msg.subject = subject
		msg.sentDate = Date()

		// TODO: Sending both plain text and html
		// http://stackoverflow.com/questions/14744197/best-practices-sending-javamail-mime-multipart-emails-and-gmail

		msg.setText(text, Charsets.UTF_8.toString())
//		msg.setContent(html, "text/html")
//		msg.dataHandler = DataHandler(ByteArrayDataSource(html, "text/html"))

		Transport.send(msg)

		return true
	}
	catch (mex: MessagingException) {
		println("\n--Exception handling in msgsendsample.java")

		mex.printStackTrace()
		println()
		var ex: Exception? = mex
		do {
			if (ex is SendFailedException) {
				val invalid = ex.invalidAddresses
				if (invalid != null) {
					println("    ** Invalid Addresses")
					for (i in invalid.indices)
						println("         " + invalid[i])
				}
				val validUnsent = ex.validUnsentAddresses
				if (validUnsent != null) {
					println("    ** ValidUnsent Addresses")
					for (i in validUnsent.indices)
						println("         " + validUnsent[i])
				}
				val validSent = ex.validSentAddresses
				if (validSent != null) {
					println("    ** ValidSent Addresses")
					for (i in validSent.indices)
						println("         " + validSent[i])
				}
			}
			println()
			if (ex is MessagingException)
				ex = ex.nextException
			else
				ex = null
		}
		while (ex != null)

		return false
	}
}


fun sendMailViaGmail(from: String, pass: String, to: String, subject: String, text: String): Boolean {
	val props = Properties()
	props.put("mail.smtp.host", "smtp.gmail.com")
	props.put("mail.smtp.port", "587")
	props.put("mail.smtp.auth", "true")
	props.put("mail.smtp.starttls.enable", "true")

	return sendMail(props, from, pass, to, subject, text)
}

fun sendMailViaYandex(from: String, pass: String, to: String, subject: String, text: String): Boolean {
	val props = Properties()
	props.put("mail.smtp.host", "smtp.yandex.ru")
	props.put("mail.smtp.port", "465")
	props.put("mail.smtp.auth", "true")
	props.put("mail.smtp.socketFactory.port", "465")
	props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")

	return sendMail(props, from, pass, to, subject, text)
}


fun receiveEmailViaImap(
		user: String,
		pass: String,
		host: String = "imap.gmail.com",
		folderName: String = "INBOX",
		limit: Int = Int.MAX_VALUE
): Array<out Message> {
	val session = Session.getInstance(Properties())
	val store = session.getStore("imaps")
	store.connect(host, user, pass)

	val folder = store.getFolder(folderName)
	folder.open(Folder.READ_ONLY)

	val count = folder.messageCount
	return folder.getMessages(Math.max(count-limit+1, 1), count)
}
