/**
 * Created by Eyal.Goldenberg on 07/12/2017.
 * Create Event Script
 */

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


Issue issue = event.issue


//MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject("ARM-1611")
String projectName = issue.getProjectObject().name
String newIssueType = issue.getIssueType().name
def user = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
def customFieldManager = ComponentAccessor.getCustomFieldManager()
IssueService issueService = ComponentAccessor.issueService

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
//if (listofComponenetName.contains("ARM_General")){
//  groupValue = "jira-administrators"
//}
//Multi component list - End

//Update Dev Responsible Team
//if (ticketStatus == "Open" || ticketStatus == "In Progress" || ticketStatus == "Reopened" ) {
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
//SmartTap Project
if (projectName == "SmartTAP") {
    if (componentValue == "AES Adapter" || componentValue == "Announcement Server" || componentValue == "Application Server" || componentValue == "Build" || componentValue == "CallDelivery Core Lib" ||
        componentValue == "CallDelivery-AL" || componentValue == "CallDelivery-CISCO" || componentValue == "CallDelivery-DS" || componentValue == "CallDelivery-IP"
        || componentValue == "CallDelivery-NGX" || componentValue == "CallDelivery-SIPREC" || componentValue == "Cisco Connector" || componentValue == "Communication Server"
        || componentValue == "Database" || componentValue == "Documentation" || componentValue == "Health Monitor" || componentValue == "Installer"
        || componentValue == "License" || componentValue == "Lync Plugin" || componentValue == "Media Delivery" || componentValue == "Media Proxy"
        || componentValue == "Media Server" || componentValue == "SmartWORKS" || componentValue == "Testware" || componentValue == "Tools") {
        groupValue = "SmartTAP Dev"

    } else if (componentValue == "SmartTAP General") {
        groupValue = "SmartTAP General"
    }
}
if (projectName == "IPP MS-UC") {
    if (componentValue == "IPP_EWS" || componentValue == "IPP_BTOE") {
        groupValue = "# IPP Oleg SW IL"
    } else if (componentValue == "IPP_UI" || componentValue == "IPP_Web") {
        groupValue = "# IPP RnD – GL"
    } else if (componentValue == "IPP_Media-USB") {
        groupValue = "# IPP Media"
    } else if (componentValue == "IPP_HRS") {
        groupValue = "# IPP Amir SW IL"
    } else if (componentValue == "IPP_Production") {
        groupValue = "# IPP Kenny SW SZ Infra"
    } else if (componentValue == "IPP_VOIP") {
        groupValue = "# IPP Allen SW SZ"
    } else if (componentValue == "IPP_Infra") {
        groupValue = "# IPP Kenny SW SZ Infra"
    } else if (componentValue == "IPP_Language") {
        groupValue = "# IPP RnD – GL"
    } else if (componentValue == "IPP_Provisioning") {
        groupValue = "# IPP Alex SW SZ"
    } else if (componentValue == "IPP_Manager") {
        groupValue = "# SW Application Dev"
    } else if (componentValue == "IPP_Logs") {
        groupValue = "# IPP Oleg SW IL"
    } else if (componentValue == "ETAS") {
        groupValue = "# IPP Support"
    } else if (componentValue == "IPP_Doc") {
        groupValue = "CI_Technical_Writers"
    } else if (componentValue == "IPP_DSP") {
        groupValue = "# IPP DSP"
    } else if (componentValue == "IPP_SPTF") {
        groupValue = "# IPP Oleg SW IL"
    } else if (componentValue == "IPP_Automation") {
        groupValue = "#IP Phone Automation"
    }
}

if (projectName == "IPP Generic SIP") {
    if (componentValue == "IPP_XSI") {
        groupValue = "# IPP Oleg SW IL"
    } else if (componentValue == "IPP_VOIP") {
        groupValue = "# IPP Allen SW SZ"
    } else if (componentValue == "IPP_UI" || componentValue == "IPP_Web") {
        groupValue = "# IPP RnD – GL"

    } else if (componentValue == "IPP_Media-USB") {
        groupValue = "# IPP Media"
    } else if (componentValue == "IPP_Production") {
        groupValue = "# IPP Kenny SW SZ Infra"
    } else if (componentValue == "IPP_Infra") {
        groupValue = "# IPP Kenny SW SZ Infra"
    } else if (componentValue == "IPP_Language") {
        groupValue = "# IPP RnD – GL"
    } else if (componentValue == "IPP_Provisioning") {
        groupValue = "# IPP Alex SW SZ"
    } else if (componentValue == "IPP_Manager") {
        groupValue = "# SW Application Dev"
    } else if (componentValue == "IPP_Logs") {
        groupValue = "# IPP Oleg SW IL"
    } else if (componentValue == "ETAS") {
        groupValue = "# IPP Support"
    } else if (componentValue == "IPP_Doc") {
        groupValue = "CI_Technical_Writers"
    } else if (componentValue == "IPP_DSP") {
        groupValue = "# IPP DSP"
    } else if (componentValue == "IPP_Automation") {
        groupValue = "#IP Phone Automation"
    }

}

if(projectName == "IPP DVF101 Linux Kernel & BSP"){
    if(componentValue == "Bootastic" || componentValue == "Linux kernel" || componentValue == "U-boot"){
        groupValue = "# IPP RnD - DSPG"}
}
if(projectName == "OVOC"){
    if(componentValue == "ETAS"){
        groupValue = "# OVOC Support"
    }else if(componentValue == "OVOC_General"){
        groupValue = "# OVOC"
    }else if(componentValue == "OVOC_CLM"){
        groupValue = "# # OVOC CLM"
    }else if(componentValue =="OVOC_QOE_SBC" || componentValue == "OVOC_QOE_SFB" || componentValue == "OVOC_QOE_IPP"|| componentValue == "OVOC_Device_BackUp"|| componentValue == "OVOC_Operator"|| componentValue == "OVOC_Reports"|| componentValue == "OVOC_PM"){
        groupValue ="# OVOC QOE"
    }else if(componentValue =="OVOC_Topology"|| componentValue == "OVOC_Alarms"|| componentValue == "OVOC_OS_Infra"|| componentValue == "OVOC_License_Pool"){
        groupValue ="# OVOC MGMT"
    }else if(componentValue =="OVOC_UI" || componentValue == "OVOC_UI_Infra"){
        groupValue ="# OVOC Web UI"
    }else if(componentValue =="IPP_MGMT"){
        groupValue ="# IPP MGMT Dev"
    }else if(componentValue =="OVOC_REST"){
        groupValue ="# OVOC"
    }else if(componentValue =="OVOC_Server_Infra"){
        groupValue ="# OVOC MGM"
    }else if(componentValue =="OVOC_QA_Automation"){
        groupValue ="# OVOC QA"
    }
}


if(projectName == "IPP Manger Express"){
    if(componentValue == "General"){
        groupValue ="# IPP MGMT Dev"
    }
}

log.debug("Project:"+projectName)
log.debug("componentValue:"+componentValue)
log.debug("IssueKey:" + issue.getKey())
log.debug("Group:" + groupValue)
/*
if (ticketStatus == "QA" || ticketStatus == "Waiting for QA" || ticketStatus == "Waiting for reproduce") {
        if (projectName == "ARM") {
            groupValue = "# ARM QA"
        }else if (projectName == "SmartTAP") {
            groupValue = "SmartTAP QA"
        }else if (projectName == "IPP Generic SIP" || projectName == "IPP MS-UC" ){
            groupValue = "IPP QA"
        }
    }
*/
IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();
GroupManager groupManager = ComponentAccessor.getGroupManager()
Group watcherGroup = groupManager.getGroup(groupValue as String)
List<Group> groupList = new ArrayList<Group>()
groupList.add(watcherGroup)

def cf = customFieldManager.getCustomFieldObjects(issue).find {it.name == 'Responsible Team'}
//return groupList.name
//issue.setCustomFieldValue(cf, groupList)
if(groupList != null){
    def missue = issue.getKey()
    def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
    def issueManager = ComponentAccessor.getIssueManager()
    def userManager = ComponentAccessor.getUserManager() as UserManager
    ApplicationUser currentUser = userManager.getUserByName('JiraAutomation')
    issue.setCustomFieldValue(customFieldManager.getCustomFieldObject('customfield_10302'),groupList.split)

    issueManager.updateIssue(currentUser, issue, EventDispatchOption.ISSUE_UPDATED, false);

    //ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

    cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), groupList.getAt(0)), changeHolder);

    log.debug("Issue Key:"+issue.getKey())
    log.debug("groupList:"+groupList.name)
    issue.store()

    issueIndexingService.reIndex ( issue as MutableIssue )
}