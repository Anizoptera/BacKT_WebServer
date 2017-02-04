package azadev.backt.webserver.mail

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


// http://www.tutorialspoint.com/java/java_sending_email.htm
// http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
// https://support.google.com/mail/answer/13287
fun sendMailViaGmail(from: String, pass: String, to: String, subject: String, text: String): Boolean {
	val props = Properties()
	props.put("mail.smtp.auth", "true")
	props.put("mail.smtp.starttls.enable", "true")
	props.put("mail.smtp.host", "smtp.gmail.com")
	props.put("mail.smtp.port", "587")

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
