
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.opensymphony.workflow.InvalidInputException





//log.error("Scrip is running")
def customFieldManager = ComponentAccessor.getCustomFieldManager()
//Issue issue = ComponentAccessor.getIssueManager().getIssueObject("SMAR-911")
def GetEpicId = customFieldManager.getCustomFieldObject("customfield_10243")
def GetEpicIdVal = issue.getCustomFieldValue(GetEpicId)
//log.error("Get Epic Id : ${GetEpicIdVal}")

//Setting Jql search
def jqlSearch = "issuekey = ${GetEpicIdVal}"
SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
def user = ComponentAccessor.userManager.getUserByName("JiraAutomation")
IssueManager issueManager = ComponentAccessor.getIssueManager()
def  issues = null
SearchService.ParseResult parseResult =  searchService.parseQuery(user, jqlSearch)
// Check if query is valid
if (parseResult.isValid()) {
    def searchResult = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
    issues = searchResult.getIssues().asImmutable()
    //log.error("Query resualt are : ${issues}")
    def issueType
    //check if list is Empty
    if(issues.isEmpty()){
        // show massage to UI
        invalidInputException = new InvalidInputException("You can\'t create a Story without an Epic as its parent, please check the \"Get Epic Id\" field")
        // log.error("The query resualt is Empty")
    }else{
        // log.error("The query resualt is Not  Empty")
        issueType = issues.getAt(0).getIssueTypeObject().name
        if(issueType == "Epic"){
            // log.error("The issue Type is Epic")
            return true
        }else{
            invalidInputException = new InvalidInputException("The link you provided is \"${issueType}\" , Please provided  \"Epic\" issue type link ")
        }
    }
}
else{
    //UserMessageUtil.error("You Cant Create Story without Epic, please check the Get Epic Id field")
    log.error("Invalid JQL: " + jqlSearch)
}
