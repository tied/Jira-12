package behivours

/**
 * Created by michaelsh on 7/10/2018.
 */


import com.atlassian.jira.component.ComponentAccessor

def action = getActionName()
log.debug("******current action ******* ${action} ")
def typeOfBug =  getFieldByName("Type of Bug").getValue()
log.debug("******type Of Bug ******* ${typeOfBug}")

if(action.contains("Create") && typeOfBug == 'Bug'){
    log.debug("**** return true****")
    def  fixVersion = getFieldById("fixversions")
    log.debug("**** Fix Version Value is  : ${fixVersion.getValue()}****")
    def affectedVersions = getFieldById("versions")
    log.debug("**** affectedVersions is ${affectedVersions.getValue()}****")
    fixVersion.setFormValue(affectedVersions.getValue())
}else{
    log.debug("**** return false****")
    false
}
