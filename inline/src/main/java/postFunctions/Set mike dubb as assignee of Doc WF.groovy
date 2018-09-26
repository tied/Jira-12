package postFunctions

/**
 * Created by michaelsh on 9/26/2018.
 */

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager

MutableIssue issue = issue
List projectListKey =['SBC','MSBR']
def issueProject = issue.getProjectObject().getName()
if(issueProject in (projectListKey)){
    def userManager = (UserManager)  ComponentAccessor.getUserManager()
    issue.setAssignee(userManager.getUser('MikeD'))
    issue.store()
}