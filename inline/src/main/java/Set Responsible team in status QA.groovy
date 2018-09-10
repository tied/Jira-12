/**
 * Created by michaelsh on 8/15/2018.
 */

/**
 * Created by Eyal.Goldenberg on 07/12/2017.
 **/

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





//MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject("SMAR-860")
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
}else if(projectName == "SBC"){
    if(componentValue =="General" || componentValue == "Infra System" || componentValue == "Infra Core" || componentValue == "Infra App" || componentValue == "BSP" ){
        groupValue = "# QA Networking"
    }else if(componentValue == "Audiomatic" || componentValue == "BatchRunner"){
        groupValue = "# QA Automation"
    }else if(componentValue == "Media Engine"){
        groupValue = "# QA Media Engine"
    }else if(componentValue == "Management"){
        groupValue ="# QA MGMT"
    }else if(componentValue == "PSTN" || componentValue == "VoipLib" || componentValue == "WireShark"){
        groupValue = "# QA PSTN"
    }else if(componentValue == "SIP" || componentValue == "SIP Infra" ||componentValue == "NGINX" || componentValue == "Stack Manager"){
        groupValue = "# QA SIP"
    }else if(componentValue == "HA" || componentValue == "LDAP"){
        groupValue = "# QA Solution"
    }
}

log.debug("groupValue:"+groupValue)

IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();
GroupManager groupManager = ComponentAccessor.getGroupManager()
Group watcherGroup = groupManager.getGroup(groupValue as String)
List<Group> groupList = new ArrayList<Group>()
groupList.add(watcherGroup)

def cf = customFieldManager.getCustomFieldObjects(issue).find {it.name == 'Responsible Team'}

//issue.setCustomFieldValue(cf, groupList)
cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), groupList), changeHolder);

issue.store()