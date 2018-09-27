package behivours



/**
 * Created by michaelsh on 9/26/2018.
 */

import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor

def currnetUser =  ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser().getUsername()
def groupManager = ComponentAccessor.getGroupManager()
def groupFlag = groupManager.isUserInGroup(currnetUser,'Application users')
def causeOfBug = getFieldById(getFieldChanged())
def DestinationStatus = getDestinationStepName()
def statusesList = ['QA', 'Ready To Submit']
if(DestinationStatus in (statusesList) && !groupFlag)
    causeOfBug.setRequired(true)ired(true)