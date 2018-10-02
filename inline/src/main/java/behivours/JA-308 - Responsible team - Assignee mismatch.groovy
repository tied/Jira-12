package behivours

/**
 * Created by michaelsh on 9/26/2018.
 */


import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.atlassian.jira.issue.history.ChangeItemBean
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.Group

def userManager = ComponentAccessor.getUserManager()
def groupManager =ComponentAccessor.getGroupManager()
def assignee = getFieldById(getFieldChanged())
def assigneeVal= assignee.getValue()
def responsibleTeam = getFieldById("customfield_10302").getValue() as String
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser().getUsername()
List usersList = ['JiraAutomation','jenkins','oracle_sc']
if(currentUser in (usersList))
    return false
else{
    def issueAction = getActionName()
    log.warn("<---|IssueKey : ${underlyingIssue.getKey()} | The assignee is : ${assignee} | ${}workflow action : ${issueAction}")
    if(!responsibleTeam || !assigneeVal || !issueAction == "Create" ){
        return false
        //log.debug("The condition failed")
    }else{
        // check if user is a member of responsible team
        def getUser = userManager.getUser(assigneeVal as String)
        log.debug("******getUser Value ******* ${getUser}")
        def getGroup = groupManager.getGroup(responsibleTeam as String)
        def userIngroup = groupManager.isUserInGroup(getUser,getGroup)
        log.debug("******The user in Group  is ******* ${userIngroup}")
        if(!userIngroup)
            assignee.setHelpText("The Assignee isn't a member of ${responsibleTeam} ")
    }
}