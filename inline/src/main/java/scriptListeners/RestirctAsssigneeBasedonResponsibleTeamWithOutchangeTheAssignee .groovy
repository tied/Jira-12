package scriptListeners

/*
**********************************
This Code used for field behaviour
**********************************
 */

/**
 * Created by michaelsh on 7/4/2018.
 */

import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.atlassian.jira.issue.history.ChangeItemBean
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.Group

def userManager = ComponentAccessor.getUserManager()
def groupManager =ComponentAccessor.getGroupManager()

def assignee = getFieldById(getFieldChanged())
def responsibleTeam =getFieldById("customfield_10302")
log.debug("******The responsible team is ******* ${responsibleTeam}")
def issueStatus = getActionName()
log.debug("******issueStatus******* ${issueStatus}")
log.debug("******The assignee is ******* ${assignee}")

if(!responsibleTeam /*|| issueStatus == "Open"*/) {
    return false
    log.debug("The condition failed")
}else{
    // check if user is a member of responsible team
    def getUser = userManager.getUserByName(assignee as String)
    log.debug("******getUser Value ******* ${getUser}")
    def getGroup = groupManager.getGroup(responsibleTeam as String)
    def userIngroup = groupManager.isUserInGroup(getUser,getGroup)
    log.debug("******The user in Group  is ******* ${userIngroup}")
    if(!userIngroup){
        assignee.setHelpText("The Assignee isn't a member of ${responsibleTeam} ")
    }else{
        return false
    }
}


def getUser = userManager.getUser()
