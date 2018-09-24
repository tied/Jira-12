package scriptListeners
/**
 * Created by michaelsh on 6/12/2018.
 */
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.index.IssueIndexManager
import com.atlassian.jira.issue.index.IssueIndexingService
import org.apache.log4j.Logger
import org.apache.log4j.Level

def findIssues(String jqlQuery) {
    def issueManager = ComponentAccessor.issueManager
    def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser.class)
    def searchProvider = ComponentAccessor.getComponent(SearchProvider.class)
    def customFieldManager = ComponentAccessor.customFieldManager
    def userManager = ComponentAccessor.getUserManager()

    def query = jqlQueryParser.parseQuery(jqlQuery)
    def results = searchProvider.search(query, user, PagerFilter.unlimitedFilter)
    results.issues.collect {  issue ->
        MutableIssue  myIssue =	issueManager.getIssueObject(issue.id)
        def singleSelectList = customFieldManager.getCustomFieldObjects(myIssue).find{it.name == "Customer"} //Select list field -Source
        def singleSelectListVal = singleSelectList.getValue(myIssue) // get the value of Select list field
        def changeHolder = new DefaultIssueChangeHolder()
        def UserPickerField  = customFieldManager.getCustomFieldObjects(issue).find{it.name == "Customer - user picker field"} // User Picker Filed -Destination
        def cleanValue = singleSelectListVal.toString().replaceAll("[^a-zA-Z0-9]","").toLowerCase() // Remove from select list value special characters
        def newUser = userManager.getUserByName(cleanValue)
        def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
        UserPickerField.updateValue(null, myIssue, new ModifiedValue(myIssue.getCustomFieldValue(UserPickerField),newUser ), new DefaultIssueChangeHolder())
        log.error("Customer: "+newUser+"Moved in"+myIssue)
        issueIndexingService.reIndex(myIssue)
    }
}

String jqlQuery = "Customer  is not EMPTY"
def issues = findIssues(jqlQuery)
