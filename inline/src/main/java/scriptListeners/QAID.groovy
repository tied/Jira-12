package scriptListeners
/**
 * Created by michaelsh on 8/28/2018.
 */

import com.atlassian.jira.ComponentManager
import org.apache.log4j.Category;
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.event.issue.IssueEventBundle
import com.atlassian.jira.event.issue.IssueEventManager
import com.atlassian.jira.event.issue.IssueEventBundleFactory
import com.atlassian.jira.event.issue.IssueEvent
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.atlassian.jira.issue.Issue

Issue issue = event.issue

//def issue = ComponentAccessor.getIssueManager().getIssueObject("VOCAVB-186")
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def  status =issue.statusObject.name
// Set list of values for waiting for custom field
def waitingForFlag =  ['Reproduce','QA Info','QAHold']
def waitingForCf = customFieldManager.getCustomFieldObject("customfield_10117")getValue(issue)
// Check issue status and waiting for values
if(status == "QA" || (status == "Waiting for" && waitingForFlag.contains(waitingForCf.toString()))){
    def qaId = customFieldManager.getCustomFieldObject("customfield_11401")
    // Check QA ID field is not empty
    if(qaId.getValue(issue)){
        log.error("QA ID IS Not empty -> Fire custom event ")
        //Fire custom event to trigger responsible team mapping script
        Long EVENT_ID = new Long("10300")
        IssueEventBundle eventBundle
        def userManager = ComponentAccessor.getUserManager()
        IssueEventManager issueEventM = ComponentAccessor.getIssueEventManager()
        IssueEventBundleFactory issueEventFactory = (IssueEventBundleFactory) ComponentAccessor.getComponent(IssueEventBundleFactory.class)
        def currentUser = userManager.getUserByName('JiraAutomation')
        eventBundle = issueEventFactory.wrapInBundle(new IssueEvent (issue, null, currentUser, EVENT_ID, true))
        issueEventM.dispatchEvent(eventBundle)
    }else{
        log.error("QA ID is  empty -> check is assignee member of responsible team")
        //check if assignee is memeber of responsible team group
        def groupManager =ComponentAccessor.getGroupManager()
        def assignne = issue.getAssigneeId()

        def responsibleTeam = customFieldManager.getCustomFieldObject("customfield_10302").getValue(issue).name[0]
        def getGroup = groupManager.getGroup(responsibleTeam as String)
        def userInGrpup = groupManager.isUserInGroup(assignne,getGroup.name)
        if(!userInGrpup){
            def assigneeDisplayName = issue?.getAssignee()?.getDisplayName()
            log.error("assignee  isnt member of responsible team -> Set error massage to UI")
            UserMessageUtil.error("""The user ${assigneeDisplayName} isnt a member of ${responsibleTeam}""")
        }else{
            log.error("assignee  is member of responsible team -> Set assignee based  on QA ID value")
            // Set Assignne based QA ID
            def changeHolder = new DefaultIssueChangeHolder()
            def user = ComponentAccessor.getJiraAuthenticationContext().loggedInUser
            def issueService = ComponentAccessor.getIssueService()
            def issueManager = ComponentAccessor.getIssueManager()
            qaId.updateValue(null,issue,new ModifiedValue(issue.getCustomFieldValue(qaId),issue.getAssigneeUser()),changeHolder)
            issueManager.updateIssue(user,issue, EventDispatchOption.ISSUE_UPDATED, false);
            def issueIndexingService = ComponentAccessor.getComponent ( IssueIndexingService )
            issueIndexingService.reIndex(issue)
        }



    }
}