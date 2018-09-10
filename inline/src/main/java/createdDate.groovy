/**
 * Created by michaelsh on 6/24/2018.
 */
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.atlassian.jira.issue.history.ChangeItemBean
import com.atlassian.jira.workflow.JiraWorkflow

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def userManager = ComponentAccessor.getUserManager()
def groupManager =ComponentAccessor.getGroupManager()
def  changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def issueManager = ComponentAccessor.getIssueManager()
def issueService = ComponentAccessor.getIssueService()
//def issue = event.issue
def issue = ComponentAccessor.getIssueManager().getIssueObject("SMAR-903")
def createdDate = issue.getCreated()
return createdDate
issue.setCreated()
