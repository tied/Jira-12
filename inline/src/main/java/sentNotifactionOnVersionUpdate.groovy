/*
package com.atlassian.mail.server
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.component.ComponentReference
import com.atlassian.jira.event.project.VersionUpdatedEvent
import com.atlassian.mail.Email
import com.atlassian.mail.queue.SingleMailQueueItem
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer

// Get version Update event
def event = event as VersionUpdatedEvent

//def originalversionName = event.getOriginalVersion().getName()
//def originalStartDate = event.getOriginalVersion().getStartDate()
//def versionStartDate  = event.version.getStartDate()

// Get version details
def versionReleaseDate = event.version.getReleaseDate()
def originalReleaseDate = event.getOriginalVersion().getReleaseDate()
def projectName = event.version.projectObject.name

//Check  relevant projects
if((projectName == "SBC" || projectName == "MSBR" )&& !originalReleaseDate.equals(versionReleaseDate)) {
    // Set Mail env
   // def userInGroup = ComponentAccessor.groupManager.getUserNamesInGroup("# SBC ETAS")
    def userManager = ComponentAccessor.userManager.getE
    MailServerManager mailServerManager = ComponentAccessor.getMailServerManager()
    SMTPMailServer mailServer = mailServerManager.getDefaultSMTPMailServer()
    def versionName = event.version.name
    def email = new Email("michael.shechter@audiocodes.com")
    email.setMimeType("text/html")
    email.setSubject("  Release Date changed at  ${versionName}")
    //def mailBody = ""
    email.setBody(text)
    mailServer.send(email)

    def userInGroup = ComponentAccessor.groupManager.getUserNamesInGroup("# SBC ETAS")
    for ( String item: userInGroup ){
            item.
    }



}










