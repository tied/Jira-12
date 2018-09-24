package scriptListeners

/**
 * Created by michaelsh on 6/14/2018.
 */
//import com.atlassian.jira.workflow.WorkflowTransitionUtil;
//import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
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
def assignee = issue.getAssignee()
def responsibleTeam = customFieldManager.getCustomFieldObject("customfield_10302")
def responsibleTeamval =  responsibleTeam.getValue(issue)
// check if responsible team is not empty
if(!responsibleTeamval){
    return  false
}else{

    // check if user is a member of responsible team
    responsibleTeamval = responsibleTeamval.name[0]
    //def user = userManager.getUserByName("JiraAdmin")
    def userIngroup = groupManager.isUserInGroup(assignee,responsibleTeamval)
    // if user not a member of the responsible team set the previous assignee
    if(!userIngroup){
        UserMessageUtil.error("The user ${assignee} isnt a member of ${responsibleTeamval}")
        def changeItems = changeHistoryManager.getChangeItemsForField(issue, "assignee");
        if(changeItems !=null && !changeItems.isEmpty()){
            ChangeItemBean ci = (ChangeItemBean) changeItems.last()
            String lastAssigne = ci.getFrom(); // name
            //def lastAssigneObj= userManager.getUserByKey(lastAssigne)
            def validateAssignResult = issueService.validateAssign(user, issue.id, lastAssigne)
            issueService.assign(user, validateAssignResult)
        }else{
            return false
        }
    }
}


