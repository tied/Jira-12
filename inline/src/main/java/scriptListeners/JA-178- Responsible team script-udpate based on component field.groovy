package scriptListeners

/**
 * Created by michaelsh on 9/28/2018.
 */


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.user.util.UserManager
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.log4j.Category
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.customfields.manager.*
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueInputParametersImpl


def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()

def issue = event.getIssue() as MutableIssue
def changelog = event.getChangeLog()

def componentFlag = event.getChangeLog().getRelated('ChildChangeItem').any{ it.field.toString().equalsIgnoreCase("Component")}
def responsibleTeamFlag = event.getChangeLog().getRelated('ChildChangeItem').any{ it.field.toString().equalsIgnoreCase("Responsible Team")}
log.error("component was changed in ${issue.key} ")
log.error("componentFlag value is  : ${componentFlag}")
log.error("responsibleTeamFlag  value is  : ${responsibleTeamFlag}")

if(componentFlag && !responsibleTeamFlag){


    String projectName = issue.getProjectObject().name
    String newIssueType = issue.getIssueType().name
    def user = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
    def customFieldManager = ComponentAccessor.getCustomFieldManager()
    IssueService issueService = ComponentAccessor.issueService
    def RTEAM = ComponentAccessor.getCustomFieldManager (  ).getCustomFieldObjectByName ( "Responsible Team Report" )

    def ticketStatus = issue.getStatusObject().getName();

    def getcomponentList = issue.getComponentObjects()
    List temp = new ArrayList()
    int count = 0
    String listofComponenetName = ""
    getcomponentList.each {
        temp [count]  = it.getName()
        count ++
        listofComponenetName += it.getName()
    }

    def issueIndexingService = ComponentAccessor.getComponent ( IssueIndexingService )
    def componentValue = issue.getComponentObjects().getAt(0)?.getName()
    def groupValue = null
    def flag = 1




    log.debug("Issue Key:"+issue.getKey())
    if (projectName == "ARM") {
        groupValue = "# ARM QA"
    }else if (projectName == "SmartTAP") {
        groupValue = "SmartTAP QA"
    }else if((projectName == "IPP Generic SIP" || projectName == "IPP MS-UC") && componentValue == "IPP_Manager"){
        groupValue = "# SW Applications QA"
        log.debug("groupValue:"+groupValue + "Component value" + componentValue)
    }else if (projectName == "IPP Generic SIP" || projectName == "IPP MS-UC" || projectName == "IPPAN" || projectName == "BIGIPP" ||  projectName == "IPP C450 Linux kernel and BSP" ){
        groupValue = "# IPP QA"
        log.debug("groupValue:"+groupValue + "Component value" + componentValue)
    }else if (projectName == "OVOC" && componentValue == "IPP_MGMT"){
        groupValue = "# SW Applications QA"
    }else if (projectName == "OVOC") {
        groupValue = "# OVOC QA"
    }else if(projectName == "IPP Manger Express") {
        groupValue = "# SW Applications QA"
    }else if(projectName == "SBC") {
        if (componentValue == "General" || componentValue == "Infra System" || componentValue == "Infra Core" || componentValue == "Infra App" || componentValue == "BSP") {
            groupValue = "# QA Networking"
        } else if (componentValue == "Audiomatic" || componentValue == "BatchRunner") {
            groupValue = "# QA Automation"
        } else if (componentValue == "Media Engine") {
            groupValue = "# QA Media Engine"
        } else if (componentValue == "Management") {
            groupValue = "# QA MGMT"
        } else if (componentValue == "PSTN" || componentValue == "VoipLib" || componentValue == "WireShark") {
            groupValue = "# QA PSTN"
        } else if (componentValue == "SIP" || componentValue == "SIP Infra" || componentValue == "NGINX" || componentValue == "Stack Manager") {
            groupValue = "# QA SIP"
        } else if (componentValue == "HA" || componentValue == "LDAP") {
            groupValue = "# QA Solution"
        }
    }

        //log.error("The group value is in the end of the if condition : ${groupValue}")


    log.debug("Project:"+projectName)
    log.debug("componentValue:"+componentValue)
    log.debug("IssueKey:" + issue.getKey())
    log.debug("Group:" + groupValue)


}



def setResponsibleTeam(String groupValue){
    IssueChangeHolder changeHolder = new DefaultIssueChangeHolder()
    GroupManager groupManager = ComponentAccessor.getGroupManager()
    Group watcherGroup = groupManager.getGroup(groupValue as String)
    List<Group> groupList = new ArrayList<Group>()
    groupList.add(watcherGroup)

    def cf = customFieldManager.getCustomFieldObjects(issue).find {it.name == 'Responsible Team'}
//return groupList.name
//issue.setCustomFieldValue(cf, groupList)
    if(groupList != null){
        def missue = issue.getKey()
        def issueManager = ComponentAccessor.getIssueManager()
        def userManager = ComponentAccessor.getUserManager() as UserManager
        ApplicationUser currentUser = userManager.getUserByName('JiraAutomation')

        issue.setCustomFieldValue(customFieldManager.getCustomFieldObject('customfield_10302'),groupList)
        issue.setCustomFieldValue(customFieldManager.getCustomFieldObject('customfield_11400'),groupList[0].name)

        issueManager.updateIssue(currentUser, issue, EventDispatchOption.ISSUE_UPDATED, false);

        //ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
        RTEAM.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(RTEAM), groupList[0].name), changeHolder);
        cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), groupList), changeHolder);

        log.debug("Issue Key:"+issue.getKey())
        log.debug("groupList:"+groupList.name)
        issue.store()

        issueIndexingService.reIndex ( issue as MutableIssue )
    }
}



