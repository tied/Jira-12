/**
 * Created by michaelsh on 9/20/2018.
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.opensymphony.workflow.InvalidInputException
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


def  userManager = ComponentAccessor.getUserManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
def groupManager =ComponentAccessor.getGroupManager()
def userUtil =  ComponentAccessor.getUserUtil()
def userService = ComponentAccessor.getComponent(UserService)
def userAdmin = userManager.getUserByName("JiraAdmin")





def tempCustomer = customFieldManager.getCustomFieldObject("customfield_10907")

//Issue issue = ComponentAccessor.getIssueManager().getIssueObject("SMAR-911")
//log.error("Get Epic Id : ${GetEpicIdVal}")

//Setting Jql search
def jqlSearch = "project = MSBR3 and \"${tempCustomer}\" is not EMPTY"
SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
def user = ComponentAccessor.userManager.getUserByName("JiraAutomation")
IssueManager issueManager = ComponentAccessor.getIssueManager()
def  issues = null
SearchService.ParseResult parseResult =  searchService.parseQuery(user, jqlSearch)
// Check if query is valid
if (parseResult.isValid()) {
    def searchResult = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
//    issues = searchResult.getIssues().asImmutable()
//    log.error("Query resualt are : ${issues}")
    log.debug("Total issues: ${searchResult.total}")
    searchResult.getIssues().each { documentIssue ->
        //log.debug(documentIssue.key)

        // if you need a mutable issue you can do:
        def issue = issueManager.getIssueObject(documentIssue.id)
        def CustomersGroup = groupManager.getGroup("Customers")
        IssueChangeHolder changeHolder = new DefaultIssueChangeHolder()
        //Get Temp Customer field
        def tempCustomerField = customFieldManager.getCustomFieldObject("customfield_10907").getValue(issue) as String

        //Get Customer-Service cloud user picker
        def customerSc =customFieldManager.getCustomFieldObject("customfield_10900")
        //Remove special characters from tempCustomerField
        //def CustomerDisplayName =tempCustomerField.replaceAll("[^a-zA-Z& ]","")
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
}
