package com.atlassian.mail.server
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.component.ComponentReference
import com.atlassian.jira.event.project.VersionUpdatedEvent
import com.atlassian.mail.Email
import com.atlassian.mail.queue.SingleMailQueueItem
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer
import java.util.Date

// Get version Update event
def event = event as VersionUpdatedEvent

// Get version details
def versionReleaseDate = event.version.getReleaseDate().format("dd/MM/yyyy")
def originalReleaseDate = event.getOriginalVersion().getReleaseDate().format("dd/MM/yyyy")
String projectName = event.version.projectObject.name
String versionName = event.version.name

//Check if Release date changed
if(!originalReleaseDate.equals(versionReleaseDate)) {
    //Set groups
    def groups = ["# All PM"]
    def groupManager = ComponentAccessor.getGroupManager()
    def i = 0
    while (i < groups.size()) {
        def members = groupManager.getUsersInGroup(groups[i])
        def j = 0
        while (j < members.size()) {
            String  userMailAdrres = members[j].getEmailAddress()
            sendMail(userMailAdrres,versionReleaseDate,originalReleaseDate,projectName,versionName)
            j++
        }
        i++
    }

}
// Set Mail env
def sendMail(String userMailAdrres, String versionReleaseDate, String originalReleaseDate,String projectName,String versionName) {
    MailServerManager mailServerManager = ComponentAccessor.getMailServerManager()
    SMTPMailServer mailServer = mailServerManager.getDefaultSMTPMailServer()

    def email = new Email(userMailAdrres)
    email.setMimeType("text/html")
    email.setSubject("project: ${projectName} , ${versionName}, Release date was changed to ${versionReleaseDate} ")
    String mailBody = ("""
    	<p>
        Jira Project: ${projectName}<br>
        Release: ${versionName}<br>
        Old Release Date: ${originalReleaseDate}<br>
        New Release Date: ${versionReleaseDate}
        </p>
        """)
    email.setBody(mailBody)
    mailServer.send(email)
    log.error("mail sent to  ${userMailAdrres}")
}







