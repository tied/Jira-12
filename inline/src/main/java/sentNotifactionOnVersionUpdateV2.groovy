//import com.atlassian.jira.component.ComponentAccessor
//import com.atlassian.jira.event.project.VersionCreateEvent
//import com.atlassian.jira.event.project.VersionUpdatedEvent
//import com.atlassian.mail.Email
//import com.atlassian.mail.queue.SingleMailQueueItem
//
//def email = new Email("some@example.com")
//
//if (event instanceof VersionUpdatedEvent){
//    def event = event.
//    event.get
//    email.setSubject("Version updated")
//    email.setBody("Version release date: ${event.version.releaseDate}")
//// email.setMimeType("text/html") // for messages with an html body
//    SingleMailQueueItem item = new SingleMailQueueItem(email)
//    ComponentAccessor.getMailQueue().addItem(item)
//}
//else if (event instanceof VersionCreateEvent){
//    def event = event as VersionCreateEvent
//    email.setSubject("Version created")
//    email.setBody("Version release date: ${event.version.releaseDate}")
//// email.setMimeType("text/html") // for messages with an html body
//    SingleMailQueueItem item = new SingleMailQueueItem(email)
//    ComponentAccessor.getMailQueue().addItem(item)
//}


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.project.VersionCreateEvent
import com.atlassian.jira.event.project.VersionUpdatedEvent
import org.ofbiz.core.entity.GenericValue


def change = event?.getChangeLog()?.getRelated("ChildChangeItem")?.find {it.field == "Release Date"}
if (change) {
    log.debug "Old value : ${change.oldstring}"
    log.debug "New value : ${change.newstring}"
    log.debug "Value changed by ${event.user} at ${event?.getChangeLog()?.created}"
    // do something if the value of the Category field changed
}