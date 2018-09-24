package scriptListeners
/**
 * Created by michaelsh on 7/18/2018.
 */

// Epic Link field

def epicLink = getFieldById(getFieldChanged())
def epicLinkVal = epicLink.getValue()
def getEpicId  = getFieldByName("Get Epic ID")
log.error(" epic link value is : ${epicLinkVal}")
epicLink.setRequired(true)
if(epicLinkVal == null){
    return false
}else{

    String subStringVal = epicLinkVal.toString().drop(4)
    getEpicId.setFormValue(subStringVal)
}



