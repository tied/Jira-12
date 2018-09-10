/**
 * Created by michaelsh on 8/8/2018.
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
def issue = ComponentAccessor.getIssueManager().getIssueObject("OVOC-1275")
def assignee = issue.getAssignee()
def project = issue.getProjectObject().getName()
def resttricedProjects = ['OVOC','BIGIPP','IPP Android-Teams','IPP C450 Linux kernel and BSP','IPP Generic SIP','IPP Manger Express','IPP MS-UC','OVOC']
def responsibleTeam = customFieldManager.getCustomFieldObject("customfield_10302")
def issueStatus =  issue.getStatus().name
def responsibleTeamval =  responsibleTeam.getValue(issue)
def assigneeGroups = groupManager.getGroupNamesForUser(assignee)
return assigneeGroups
// check if responsible team is not empty
if(!responsibleTeamval || !resttricedProjects.contains(project)  ) {
    return false
}else{
    // check if user is a member of responsible team
    responsibleTeamval = responsibleTeamval.name[0]
    //def user = userManager.getUserByName("JiraAdmin")
    def userIngroup = groupManager.isUserInGroup(assignee,responsibleTeamval)
    // if user not a member of the responsible team set the previous assignee
    if(!userIngroup){
        //def assigneeGroups = groupManager.getGroupNamesForUser(assignee)
        UserMessageUtil.error("""The user ${assignee} isnt a member of ${responsibleTeamval}
        Please set The resplonsible team value to one of the flowwing options:
        
        
        """)
    }else{
        return false
    }
}
