package scriptListeners

/**
 * Created by michaelsh on 9/22/2018.
 */



/*



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
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.customfields.manager.*
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue



Issue issue = event.issue as MutableIssue

def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def changelog = event.getChangeLog()

def componentFlag = event.getChangeLog().getRelated('ChildChangeItem').any{ it.field.toString().equalsIgnoreCase("Component")}
def responsibleTeamFlag = event.getChangeLog().getRelated('ChildChangeItem').any{ it.field.toString().equalsIgnoreCase("Responsible Team")}
log.error("component was changed in ${issue.key} ")
log.error("componentFlag value is  : ${componentFlag}")
log.error("responsibleTeamFlag  value is  : ${responsibleTeamFlag}")




//MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject("ARM-1199")
String projectName = issue.getProjectObject().name
String newIssueType = issue.getIssueType().name

def ticketStatus = issue.getStatusObject().getName();
def user = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
//return ticketStatus


// Check if ticket status is QA or Waiting for
def waitingfor = customFieldManager.getCustomFieldObjectByName("Waiting for")
waitingfor = issue.getCustomFieldValue(waitingfor)
def waitingforbol = false
if(ticketStatus == "Waiting for" & (waitingfor == "Reproduce" || waitingfor == "QA Info")){
    waitingforbol = true
}

if(ticketStatus == "QA" ||  waitingforbol ){
    if(!responsibleTeamFlag){
        //Set Responsible team
    }

}else if(componentFlag && ){

}




def RTEAM = ComponentAccessor.getCustomFieldManager (  ).getCustomFieldObjectByName ( "Responsible Team Report" )
def customFieldManager = ComponentAccessor.getCustomFieldManager()

//Multi component list - Start
def getcomponentList = issue.getComponentObjects()
List temp = new ArrayList()
int count = 0
String listofComponenetName = ""
getcomponentList.each {
    temp [count]  = it.getName()
    count ++
    listofComponenetName += it.getName()
}
//def customFieldManager = ComponentAccessor.getCustomFieldManager()
IssueService issueService = ComponentAccessor.issueService
def issueIndexingService = ComponentAccessor.getComponent ( IssueIndexingService )

def componentValue = issue.getComponentObjects().getAt(0)?.getName()
//return componentValue
def groupValue = null
def flag = 1
//if (listofComponenetName.contains("ARM_General")){
//  groupValue = "jira-administrators"
//}
//Multi component list - End

//Update Dev Responsible Team

//Michael-disable if condition

if (ticketStatus == "Reopened") {
    //ARM Project
    if (projectName == "ARM") {
        if (componentValue == "ARM_Configurator") {
            //return projectName
            // --- CHANGE GROUP NAME !!!!
            groupValue = "# RnD ARM Configurator"
            //  flag =2
        } else if (componentValue == "ARM_General") {
            groupValue = "# RnD ARM General"
        } else if (componentValue == "ARM_UI") {
            groupValue = "# RnD ARM UI"
        } else if (componentValue == "ARM_Router") {
            groupValue = "# RnD ARM Router"
        }
    }
    //SmartTAP Project
    else if (projectName == "SmartTAP"){
        if (componentValue == "AES Adapter" || componentValue == "Announcement Server" || componentValue == "Application Server" || componentValue == "Build" || componentValue == "CallDelivery Core Lib" ||
            componentValue == "CallDelivery-AL" || componentValue == "CallDelivery-CISCO" || componentValue == "CallDelivery-DS" || componentValue == "CallDelivery-IP"
            || componentValue == "CallDelivery-NGX" || componentValue == "CallDelivery-SIPREC" || componentValue == "Cisco Connector" || componentValue == "Communication Server"
            || componentValue == "Database" || componentValue == "Documentation" || componentValue == "Health Monitor" || componentValue == "Installer"
            || componentValue == "License" || componentValue == "Lync Plugin" || componentValue == "Media Delivery" || componentValue == "Media Proxy"
            || componentValue == "Media Server" || componentValue == "SmartWORKS" || componentValue == "Testware" || componentValue == "Tools"){
            groupValue = "SmartTAP Dev"

        }else if (componentValue == "SmartTAP General") {
            groupValue = "SmartTAP General"
        }
    }
    else if (projectName == "IPP MS-UC"){
        if (componentValue == "IPP_EWS" || groupValue == "IPP_BTOE"){
            groupValue = "# IPP Oleg SW IL"
        }else if (componentValue == "IPP_UI" || componentValue == "IPP_Web"){
            groupValue = "# IPP RnD – GL"
        }else if (componentValue == "IPP_Media-USB"){
            groupValue = "# IPP Media"
        }else if (componentValue == "IPP_HRS"){
            groupValue = "# IPP Oriel SW IL"
        }else if (componentValue == "IPP_Production"){
            groupValue = "# IPP Kenny SW SZ Infra"
        }else if (componentValue == "IPP_VOIP"){
            groupValue = "# IPP Allen SW SZ"
        }else if (componentValue == "IPP_Infra"){
            groupValue = "# IPP Kenny SW SZ Infra"
        }else if (componentValue == "IPP_Language"){
            groupValue = "# IPP RnD – GL"
        }else if (componentValue == "IPP_Provisioning"){
            groupValue = "# IPP Alex SW SZ"
        }else if (componentValue == "IPP_Manager"){
            groupValue = "# IPP Oleg SW IL"
        }else if (componentValue == "IPP_Logs"){
            groupValue = "# IPP Oleg SW IL"
        }else if (componentValue == "ETAS"){
            groupValue = "# IPP Support"
        }else if (componentValue == "IPP_Doc"){
            groupValue = "CI_Technical_Writers"
        }else if (componentValue == "IPP_DSP"){
            groupValue = "# IPP DSP"
        }   else if (componentValue == "IPP_SPTF"){
            groupValue = "# IPP Oleg SW IL"
        }



    }else if (projectName == "IPP Generic SIP"){
        if (componentValue == "IPP_XSI"){
            groupValue = "# IPP Oleg SW IL"
        }else if (componentValue == "IPP_VOIP"){
            groupValue = "# IPP Allen SW SZ"
        }else if (componentValue == "IPP_UI"||componentValue == "IPP_Web"){
            groupValue = "# IPP RnD – GL"

        }else if (componentValue == "IPP_Media-USB"){
            groupValue = "# IPP Media"
        }else if (componentValue == "IPP_Production"){
            groupValue = "# IPP Kenny SW SZ Infra"
        }else if (componentValue == "IPP_Infra"){
            groupValue = "# IPP Kenny SW SZ Infra"
        }else if (componentValue == "IPP_Language"){
            groupValue = "# IPP RnD – GL"
        }else if (componentValue == "IPP_Provisioning"){
            groupValue = "# IPP Alex SW SZ"
        }else if (componentValue == "IPP_Manager"){
            groupValue = "# IPP Oleg SW IL"
        }else if (componentValue == "IPP_Logs"){
            groupValue = "# IPP Oleg SW IL"
        }else if (componentValue == "ETAS"){
            groupValue = "# IPP Support"
        }else if (componentValue == "IPP_Doc"){
            groupValue = "CI_Technical_Writers"
        }else if (componentValue == "IPP_DSP"){
            groupValue = "# IPP DSP"
        }
    }
}

// Michael- end if condition


def waitingforbol = false
if(ticketStatus == "Waiting for" & (waitingfor == "Reproduce" || waitingfor == "QA Info")){
    waitingforbol = true
}

if (ticketStatus == "QA" || waitingforbol) {
    log.debug("Issue Key:"+issue.getKey())
    if (projectName == "ARM") {
        groupValue = "# ARM QA"
    }else if (projectName == "SmartTAP") {
        groupValue = "SmartTAP QA"
    }else if((projectName == "IPP Generic SIP" || projectName == "IPP MS-UC") && componentValue == "IPP_Manager"){
        groupValue = "# SW Applications QA"
        log.debug("groupValue:"+groupValue + "Component value" + componentValue)
    }else if (projectName == "IPP Generic SIP" || projectName == "IPP MS-UC" ){
        groupValue = "# IPP QA"
        log.debug("groupValue:"+groupValue + "Component value" + componentValue)
    }else if (projectName == "OVOC" && componentValue == "IPP_MGMT"){
        groupValue = "# SW Applications QA"
    }else if (projectName == "OVOC") {
        groupValue = "# OVOC QA"
    }else if(projectName == "IPP Manger Express") {
        groupValue = "# SW Applications QA"
    }
    log.debug("groupValue:"+groupValue)


    IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();
    GroupManager groupManager = ComponentAccessor.getGroupManager()
    Group watcherGroup = groupManager.getGroup(groupValue as String)
    List<Group> groupList = new ArrayList<Group>()
    groupList.add(watcherGroup)

    def cf = customFieldManager.getCustomFieldObjects(issue).find {it.name == 'Responsible Team'}

//issue.setCustomFieldValue(cf, groupList)
    def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
    def issueManager = ComponentAccessor.getIssueManager()
    def userManager = ComponentAccessor.getUserManager() as UserManager
    ApplicationUser currentUser = userManager.getUserByName('JiraAutomation')
    issue.setCustomFieldValue(customFieldManager.getCustomFieldObject('customfield_10302'),groupList)
    issue.setCustomFieldValue(customFieldManager.getCustomFieldObject('customfield_11100'),groupList[0].name)

    issueManager.updateIssue(currentUser, issue, EventDispatchOption.ISSUE_UPDATED, false);
    RTEAM.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(RTEAM), groupList[0].name), changeHolder);
    cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), groupList), changeHolder);
    issue.store()
    issueIndexingService.reIndex ( issue as MutableIssue )
}
*/