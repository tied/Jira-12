package postFunctions



/**
 * Created by Eyal.Goldenberg on 07/12/2017.
 */



import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.index.IssueIndexManager
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.log4j.Category
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.user.util.UserManager


//Issue issue = event.issue


//MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject("ARM-1199")
String projectName = issue.getProjectObject().name
String newIssueType = issue.getIssueType().name

def ticketStatus = issue.getStatusObject().getName();

//return ticketStatus

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def waitingfor = customFieldManager.getCustomFieldObjectByName("Waiting for")
waitingfor = issue.getCustomFieldValue(waitingfor)

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


def componentValue = issue.getComponentObjects().getAt(0)?.getName()
def groupValue = null
def flag = 1
//if (listofComponenetName.contains("ARM_General")){
//  groupValue = "jira-administrators"
//}
//Multi component list - End

//Update Dev Responsible Team
//if (ticketStatus == "Reopened") {
//ARM Project
if (projectName == "ARM") {
    if (componentValue == "ARM_Configurator") {
        groupValue = "# RnD ARM Configurator"
    } else if (componentValue == "ARM_General") {
        groupValue = "# RnD ARM General"
    } else if (componentValue == "ARM_UI") {
        groupValue = "# RnD ARM UI"
    } else if (componentValue == "ARM_Router") {
        groupValue = "# RnD ARM Router"
    }
}
//SmartTab Project
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
        groupValue = "“# IPP Oriel SW IL"
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
    }else if (componentValue == "IPP_ETAS"){
        groupValue = "# IPP Support"
    }else if (componentValue == "IPP_Doc"){
        groupValue = "CI_Technical_Writers"
    }else if (componentValue == "Connectivity"){
        groupValue = "# IPP Oleg SW IL"
    }else if (componentValue == "IPP_DSP") {
        groupValue = "# IPP DSP"
    } else if (componentValue == "IPP_SPTF") {
        groupValue = "# IPP Oleg SW IL"
    } else if (componentValue == "IPP_Automation") {
        groupValue = "#IP Phone Automation"
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
    }else if (componentValue == "IPP_ETAS"){
        groupValue = "# IPP Support"
    }else if (componentValue == "IPP_Doc"){
        groupValue = "CI_Technical_Writers"
    }else if (componentValue == "Connectivity"){
        groupValue = "# IPP Oleg SW IL"

    }
}
else if (projectName == "SBC GW and MSBR") {
    if (componentValue == "SBC_SIP") {
        groupValue = "test-sbc-sip"
    } else if (componentValue == "SBC_Media") {
        groupValue = "test-sbc-media"
    }
}
//}
def waitingforbol = false
if(ticketStatus == "Waiting for" & (waitingfor == "Reproduce" || waitingfor == "QA Info")){
    waitingforbol = true
}

if (ticketStatus == "QA" || waitingforbol || ticketStatus == "Waiting for reproduce") {
    if (projectName == "ARM") {
        groupValue = "# ARM QA"
    }else if (projectName == "SmartTAP") {
        groupValue = "SmartTAP QA"
    }else if (projectName == "IPP Generic SIP" || projectName == "IPP MS-UC" ){
        groupValue = "# IPP QA"
    }else if (projectName == "SBC GW and MSBR"){
        groupValue = "test-sbc-qa"
    }
}

IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();
GroupManager groupManager = ComponentAccessor.getGroupManager()
Group watcherGroup = groupManager.getGroup(groupValue as String)
List groupList = new ArrayList()
groupList.add(watcherGroup)

def cf = customFieldManager.getCustomFieldObjects(issue).find {it.name == 'Responsible Team'}

//issue.setCustomFieldValue(cf, groupList)
cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), groupList), changeHolder);

issue.store()