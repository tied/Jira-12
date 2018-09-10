/**
 * Created by michaelsh on 6/13/2018
 * This Scrip will validate reporter based on group membership
 * located on create Transition
 */


import com.atlassian.jira.component.ComponentAccessor

def Groupmanager =ComponentAccessor.getGroupManager()
def issue = ComponentAccessor.getIssueManager().getIssueObject("IGS-864")
def issueReporter = issue.creatorId
def groupMembership = Groupmanager.getGroupNamesForUser(issueReporter)
def userMembership = groupMembership.any{group ->
    group.contains("# All Support") || group.contains("# All PreSales")
}
(userMembership) ? true : false
