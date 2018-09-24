package console
/**
 * Created by michaelsh on 9/20/2018.
 */


import com.atlassian.jira.bc.JiraServiceContextImpl
import com.atlassian.jira.bc.filter.SearchRequestService
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchRequest
import com.atlassian.jira.sharing.SharePermissionImpl
import com.atlassian.jira.sharing.SharedEntity
import com.atlassian.jira.sharing.type.ShareType
import com.atlassian.sal.api.component.ComponentLocator

def searchRequestService = ComponentLocator.getComponent(SearchRequestService)
def user = ComponentAccessor.jiraAuthenticationContext?.getLoggedInUser()
def searchService = ComponentAccessor.getComponent(SearchService)

def serviceContext = new JiraServiceContextImpl(user)

def parseResult = searchService.parseQuery(user, "project = JA")
if (parseResult.isValid()) {

    // create the search request
    def query = parseResult.query
    def searchRequest = new SearchRequest(query, user, "Michael-filter-script", "created with script")

    // set shares
    def sharePerm = new SharePermissionImpl(null, ShareType.Name.GROUP, "jira-administrators", null)
    searchRequest.setPermissions(new SharedEntity.SharePermissions([sharePerm] as Set))

    // store the search request
    searchRequestService.createFilter(serviceContext, searchRequest)
}