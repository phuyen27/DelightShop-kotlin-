package com.example.phamthiphuonguyen_delightshop.Activity

import java.util.*
import javax.activation.DataHandler
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GmailSender(
    private val recipientEmail: String,
    private val emailContent: String,
    private val subject: String // Thêm tham số subject
) : Thread() {

    override fun run() {
        try {
            val username = "delightshophcm@gmail.com"
            val password = "sygj vbgf gori cggx"

            val props = Properties()
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "465"

            val session = Session.getDefaultInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password)
                }
            })

            val message = MimeMessage(session)
            message.setFrom(InternetAddress(username))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
            message.subject = subject // Sử dụng tham số subject
            message.setText(emailContent) // Sử dụng tham số emailContent

            Transport.send(message)

        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}