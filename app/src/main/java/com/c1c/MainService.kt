package com.c1c

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.google.android.gms.fido.fido2.api.common.Attachment
import java.util.*
import javax.activation.DataHandler
import javax.mail.*
import javax.mail.internet.*


class SendMail(ctext: Context, //Information to send email
               private val email: String, private val subject: String, private val message: String
) :
    AsyncTask<Void?, Void?, Void?>() {


    private val context: Context = ctext
    private var session: Session? = null

    //Class Constructor
    init {
        //Initializing variables
    }

    //Progressdialog to show while sending email
    private var progressDialog: ProgressDialog? = null
    override fun onPreExecute() {
        super.onPreExecute()
        //Showing progress dialog while sending email
        progressDialog =
            ProgressDialog.show(context, "Sending message", "Please wait...", false, false)
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        //Dismissing the progress dialog
        progressDialog!!.dismiss()
        //Showing a success message
        Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        //Creating properties
        val props = Properties()

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props["mail.smtp.host"] = "smtp.gmail.com"// needed for gmail
        props["mail.smtp.ssl.enable"] = "true"
        props["mail.smtp.socketFactory.port"] = "465"
//        props["mail.smtp.socketFactory.port"] = "465"
//        props["mail.user"] = "acc636083@gmail.com"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"
//        props["mail.smtp.port"] = "465"

        //Creating a new session
        session = Session.getInstance(props,
            object : Authenticator() {
                //Authenticating the password
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("hbabydev09@gmail.com", "Og@1234567")
                }
            })
//        session = Session.getInstance(props, null)
        try {
            //Creating MimeMessage object
            session!!.debug = true
            val mm = MimeMessage(session)

            //Setting sender address
            mm.setFrom(InternetAddress("hbabydev09@gmail.com"))
//            mm.setFrom(InternetAddress("acc636083@gmail.com"))
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(email))
            //Adding subject
            mm.subject = subject
            //Adding message
            mm.setText(message)

            //Sending email
            Transport.send(mm)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return null
    }


}


class MailService  {
    // public static final String MAIL_SERVER = "localhost";
    var toList: String
    var ccList: String?
    var bccList: String?
    var subject: String
    private var from: String
    private var txtBody: String
    private var htmlBody: String
    var replyToList: String?
    private var attachments: ArrayList<Attachment?>?
    var isAuthenticationRequired = false

    constructor(
        from: String, toList: String, subject: String, txtBody: String, htmlBody: String,
        attachment: Attachment? = null
    ) {
        this.txtBody = txtBody
        this.htmlBody = htmlBody
        this.subject = subject
        this.from = from
        this.toList = toList
        ccList = null
        bccList = null
        replyToList = null
        isAuthenticationRequired = true
        attachments = ArrayList<Attachment?>()
        if (attachment != null) {
            attachments!!.add(attachment)
        }
    }



    @Throws(AddressException::class, MessagingException::class)
    fun sendAuthenticated() {
        isAuthenticationRequired = true
        send()
    }

    /**
     * Send an e-mail
     *
     * @throws MessagingException
     * @throws AddressException
     */
    @Throws(AddressException::class, MessagingException::class)
    fun send() {
        val props = Properties()

        // set the host smtp address
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.user"] = from
        props["mail.smtp.starttls.enable"] = "true" // needed for gmail
        props["mail.smtp.auth"] = "true" // needed for gmail
        props["mail.smtp.port"] = "587" // gmail smtp port

        /*Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mobile@mydomain.com", "mypassword");
            }
        };*/
        val session: Session
        if (isAuthenticationRequired) {
            val auth: Authenticator = SMTPAuthenticator()
            props["mail.smtp.auth"] = "true"
            session = Session.getDefaultInstance(props, auth)
        } else {
            session = Session.getDefaultInstance(props, null)
        }

        // get the default session
        session.debug = true

        // create message
        val msg: Message = MimeMessage(session)

        // set from and to address
        try {
            msg.setFrom(InternetAddress(from, from))
            msg.replyTo = arrayOf(InternetAddress(from, from))
        } catch (e: Exception) {
            msg.setFrom(InternetAddress(from))
            msg.replyTo = arrayOf(InternetAddress(from))
        }

        // set send date
        msg.sentDate = Calendar.getInstance().time

        // parse the recipients TO address
        var st = StringTokenizer(toList, ",")
        val numberOfRecipients = st.countTokens()
        val addressTo = arrayOfNulls<InternetAddress>(numberOfRecipients)
        var i = 0
        while (st.hasMoreTokens()) {
            addressTo[i++] = InternetAddress(
                st
                    .nextToken()
            )
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo)

        // parse the replyTo addresses
        if (replyToList != null && "" != replyToList) {
            st = StringTokenizer(replyToList, ",")
            val numberOfReplyTos = st.countTokens()
            val addressReplyTo = arrayOfNulls<InternetAddress>(numberOfReplyTos)
            i = 0
            while (st.hasMoreTokens()) {
                addressReplyTo[i++] = InternetAddress(
                    st.nextToken()
                )
            }
            msg.replyTo = addressReplyTo
        }

        // parse the recipients CC address
        if (ccList != null && "" != ccList) {
            st = StringTokenizer(ccList, ",")
            val numberOfCCRecipients = st.countTokens()
            val addressCC = arrayOfNulls<InternetAddress>(numberOfCCRecipients)
            i = 0
            while (st.hasMoreTokens()) {
                addressCC[i++] = InternetAddress(
                    st
                        .nextToken()
                )
            }
            msg.setRecipients(Message.RecipientType.CC, addressCC)
        }

        // parse the recipients BCC address
        if (bccList != null && "" != bccList) {
            st = StringTokenizer(bccList, ",")
            val numberOfBCCRecipients = st.countTokens()
            val addressBCC = arrayOfNulls<InternetAddress>(numberOfBCCRecipients)
            i = 0
            while (st.hasMoreTokens()) {
                addressBCC[i++] = InternetAddress(
                    st
                        .nextToken()
                )
            }
            msg.setRecipients(Message.RecipientType.BCC, addressBCC)
        }

        // set header
        msg.addHeader("X-Mailer", "MyAppMailer")
        msg.addHeader("Precedence", "bulk")
        // setting the subject and content type
        msg.subject = subject
        val mp: Multipart = MimeMultipart("related")

        // set body message
        val bodyMsg = MimeBodyPart()
        bodyMsg.setText(txtBody, "iso-8859-1")
        if (attachments!!.size > 0) htmlBody =
            htmlBody.replace("#filename#".toRegex(), attachments!![0]!!.name)
        if (htmlBody.indexOf("#header#") >= 0) htmlBody =
            htmlBody.replace("#header#".toRegex(), attachments!![1]!!.name)
        if (htmlBody.indexOf("#footer#") >= 0) htmlBody =
            htmlBody.replace("#footer#".toRegex(), attachments!![2]!!.name)
        bodyMsg.setContent(htmlBody, "text/html")
        mp.addBodyPart(bodyMsg)

        // set attachements if any
        if (attachments != null && attachments!!.size > 0) {
            i = 0
//            while (i < attachments!!.size) {
//                val a: Attachment? = attachments!![i]
//                val att: BodyPart = MimeBodyPart()
//                att.dataHandler = DataHandler(a.da)
//                att.fileName = a.getFilename()
//                att.setHeader("Content-ID", "<" + a.getFilename().toString() + ">")
//                mp.addBodyPart(att)
//                i++
//            }
        }
        msg.setContent(mp)

        // send it
        try {
            Transport.send(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * SimpleAuthenticator is used to do simple authentication when the SMTP
     * server requires it.
     */
    private class SMTPAuthenticator : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("acc636083@gmail.com", "Og@1234567")
        }
    }

    fun setFrom(from: String) {
        this.from = from
    }

    fun setTxtBody(body: String) {
        txtBody = body
    }

    fun setHtmlBody(body: String) {
        htmlBody = body
    }

    companion object {
//        private val SMTP_SERVER: String = DataService
//            .getSetting(DataService.SETTING_SMTP_SERVER)
    }
}
