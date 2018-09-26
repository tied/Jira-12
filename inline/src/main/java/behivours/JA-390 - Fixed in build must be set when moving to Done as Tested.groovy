package behivours

/**
 * Created by michaelsh on 9/26/2018.
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.resolution.Resolution

//Get issueType
def issueType = underlyingIssue.getIssueType()
//Get current user
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser().getUsername()
def resolution = getFieldById('Resolution')?.getValue() as Resolution
def workflowDestination = getDestinationStep().getName()
//Get fixInBuild field
def fixInBuild = getFieldById(getFieldChanged())
//Get a list of issueTypes
List issueTypesArr = ['Epic','Story','SubTask','Bug']
log.warn("<---|IssueKey : ${underlyingIssue.getKey()} | issueType is : ${issueType.name} | workflow Destination is : ${workflowDestination} | issue Resolution is : ${resolution}--->")
//if one of the users is :oracle_sc, jenkins,JiraAutomation, exit
if(!(currentUser == "oracle_sc" || currentUser == "jenkins" || currentUser == "JiraAutomation")) {
    if ( issueType.name == 'Bug' && workflowDestination == 'QA')
        fixInBuild.setRequired(true)
    else if(workflowDestination =='Done' && issueType in(issueTypesArr) && resolution =='Tested' )
        fixInBuild.setRequired(true)