package scriptListeners
/**
 * Created by michaelsh on 8/30/2018.
 */

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.ComponentManager
import org.apache.log4j.Category;
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.index.IssueIndexingService
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.atlassian.jira.issue.Issue


MutableIssue issue = event.issue

//MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject("VOCAVB-186")
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def  status = issue.statusObject.name
// Set list of values for waiting for custom field
def waitingForFlag =  ['Reproduce','QA Info','QAHold']
def waitingForCf = customFieldManager.getCustomFieldObject("customfield_10117")getValue(issue)
// Check issue status and waiting for values
if(status == "QA" || (status == "Waiting for" && waitingForFlag.contains(waitingForCf.toString()))){
    def qaId = customFieldManager.getCustomFieldObject("customfield_11401")
    // return qaId.getValue(issue).key
    // Check QA ID field is not empty
    if(qaId?.getValue(issue)){
        log.error("QA is not empty -> Set assignee based  on QA ID value")
        // Set Assignne based QA ID
        def changeHolder = new DefaultIssueChangeHolder()
        issue.setAssigneeId(qaId.getValue(issue).key)
        def userManager = ComponentAccessor.getUserManager()
        def user = userManager.getUserByName('JiraAutomation')
        def issueManager = ComponentAccessor.getIssueManager()
        issueManager.updateIssue(user,issue, EventDispatchOption.ISSUE_UPDATED, false);
    }
}




