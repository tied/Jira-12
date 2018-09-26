package behivours

/**
 * Created by michaelsh on 9/26/2018.
 */


//Should be updated by SW eng – Mandatory when moving to ReadyToSubmit or QA or when moving to Done as Tested

def causeOfBug = getFieldById(getFieldChanged())
def currentAction = getActionName()
def statuses = ['QA', 'ReadyToSubmit']
if(currentAction in (statuses))
    causeOfBug.setRequired(true)