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
}else if(projectName == "IPP DVF101 Linux Kernel & BSP") {
    if (componentValue == "Bootastic" || componentValue == "Linux kernel" || componentValue == "U-boot") {
        groupValue = "# IPP RnD - DSPG"
    }
}else if(projectName == "OVOC"){
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

}else if(projectName == "IPP Manger Express"){
    if(componentValue == "General"){
        groupValue ="# IPP MGMT Dev"
    }
}else if(projectName == "SBC"){
    if(componentValue == "General"){
        groupValue ="# SBC Infra"
    }else if(componentValue == "Infra System"){
        groupValue ="# SBC Infra System"
    }else if(componentValue == "Infra Core"){
        groupValue ="# SBC Infra Core"
    }else if(componentValue == "Infra App"){
        groupValue ="# SBC Infra App"
    }else if (componentValue == "BSP"){
        groupValue ="# SBC BSP"
    }else if(componentValue == "Management"){
        groupValue ="# SBC OAM"
    }else if(componentValue == "SBC Networking"){
        groupValue ="# SBC Networking"
    }else if(componentValue =="HA"){
        groupValue ="# SBC HA"
    }else if(componentValue =="SIP"){
        groupValue ="# SBC SIP"
    }else if(componentValue =="SIP Infra"){
        groupValue ="# SBC SIP Infra"
    }else if(componentValue =="Media Engine"){
        groupValue ="# SBC ME"
    }else if(componentValue == "PSTN"){
        groupValue ="# PSTN"
    }else if(componentValue =="VoipLib"){
        groupValue ="# VoipLib"
    }else if(componentValue =="LDAP"){
        groupValue ="# SBC LDAP"
    }else if(componentValue =="Feature Key"){
        groupValue ="# SBC BSP"
    }else if(componentValue =="NGINX"){
        groupValue ="# SBC SIP"
    }else if(componentValue =="Stack Manager"){
        groupValue ="# SBC SysEng"
    }else if(componentValue =="Desktop Applications"){
        groupValue ="# SBC SysEng"
    }else if(componentValue =="Audiomatic"){
        groupValue ="# QA Automation"
    }else if(componentValue =="BatchRunner"){
        groupValue ="# QA Automation"
    }else if(componentValue =="WireShark"){
        groupValue ="# SBC WireShark"
    }else if(componentValue == "# Board ETAS"){
        groupValue ="# Board ETAS"
    }else if(componentValue =="ETAS"){
        groupValue ="# SBC ETAS"
    }else if(componentValue == "Cloud"){
        groupValue = "# SBC SysEng"
    }
}else if(projectName == "MSBR"){
    groupValue == "# MSBR"
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