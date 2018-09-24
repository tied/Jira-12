package scriptListeners
/**
 * Created by michaelsh on 5/17/2018.
 */
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.event.type.EventDispatchOption
import org.apache.log4j.Logger
import org.apache.log4j.Level

Issue issue = event.issue
def  userManager = ComponentAccessor.getUserManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
def groupManager =ComponentAccessor.getGroupManager()
def userUtil =  ComponentAccessor.getUserUtil()
def userService = ComponentAccessor.getComponent(UserService)
def userAdmin = userManager.getUserByName("JiraAdmin")
//Get issueMnager
def issueManager = ComponentAccessor.getIssueManager()
//Issue issue = ComponentAccessor.getIssueManager().getIssueObject("ARM-1596")
def issueType = issue.getIssueType().name
//Get Type of bug field
def typeOfBug = customFieldManager.getCustomFieldObject("customfield_10107").getValue(issue) as String
// Get Creator
def issueCreator = issue.getCreator().name
//Check if the issue is field bug and oracle service cloud is the creator
if(issueType =="Bug" && typeOfBug == "FieldBug" && issueCreator == "oracle_sc"){
    //Get Customers group
    def CustomersGroup = groupManager.getGroup("Customers")
    IssueChangeHolder changeHolder = new DefaultIssueChangeHolder()
    //Get Temp Customer field
    def tempCustomerField = customFieldManager.getCustomFieldObject("customfield_10803").getValue(issue) as String
    //Get Customer-Service cloud user picker
    def customerSc =customFieldManager.getCustomFieldObject("customfield_10700")
    //Remove special characters from tempCustomerField
    //def CustomerDisplayName = tempCustomerField.replaceAll("[^a-zA-Z& ]","")
    def CustomerDisplayName =tempCustomerField.replaceAll("[^a-zA-Z0-9& ]","")
    def CustomerUserName = tempCustomerField.replaceAll("[^a-zA-Z0-9]","").toLowerCase()
    //check  The user is customer group member
    if(!groupManager.getUserNamesInGroup(CustomersGroup).contains(CustomerUserName)){
        //Create new user
        UserService.CreateUserRequest createUserRequest = UserService.CreateUserRequest.
            withUserDetails(userAdmin, CustomerUserName, "Aa123456", CustomerUserName+"@rndcustomers.local", CustomerDisplayName).withNoApplicationAccess()
        UserService.CreateUserValidationResult newUser = userService.validateCreateUser(createUserRequest)
        if(newUser.isValid()) {
            userService.createUser(newUser)
            //Add new user to Costumer group
            ApplicationUser createdUser = userManager.getUserByName(CustomerUserName)
            userUtil.addUserToGroup(CustomersGroup,createdUser)
        }else{
            log.info(newUser.getErrorCollection())
        }
    }
    //Set user to Customer-Service cloud field
    ApplicationUser userObj =  userManager.getUserByName(CustomerUserName)
    customerSc.updateValue(null,issue,new  ModifiedValue(issue.getCustomFieldValue(customerSc),userObj), changeHolder)
    //issueManager.updateIssue(userAdmin,issue,EventDispatchOption.ISSUE_UPDATED,false)
    issue.store()
}
issue.creator
