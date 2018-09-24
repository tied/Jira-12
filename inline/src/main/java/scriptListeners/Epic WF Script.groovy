package scriptListeners

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.config.ResolutionManager

def log = Logger.getLogger("com.methoda.test")
log.setLevel(Level.DEBUG)

//def issue = ComponentAccessor.getIssueManager().getIssueObject("IGS-975")

Issue issue = event.issue
log.debug("issue key:"+issue.getKey())
def status

//def user = event.getUser().name
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def resoultion
//return issue.getResolution()
if(issue.getResolution() != null){
    resoultion =  	issue.getResolution().name
}
def resolutionManager = ComponentAccessor.getComponent(ResolutionManager)


def query = jqlQueryParser.parseQuery("issueFunction in linkedIssuesOfAllRecursiveLimited(" + "'issue=" + issue.key + "',1)" + "AND issuetype in(Story)")
log.info("The Query is : ${query}")
def results = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())
def Stories = results.getIssues()
log.info("The Query is : ${query}")
def StoriesStatus = []
Stories.each{
    StoriesStatus.add(it.status.name)

}
log.info("The Stories List val : ${StoriesStatus}")

def query1 = jqlQueryParser.parseQuery("issueFunction in linkedIssuesOfAllRecursiveLimited(" + "'issue=" + issue.key + "',1)" + "AND issuetype in(Test)")
log.info("The Query for test is : ${query1}")
def results1 = searchProvider.search(query1, user, PagerFilter.getUnlimitedFilter())
def test = results1.getIssues()
log.info("The Query results for test : ${test}")
def teststatus = test[0].status.name
def testresoultion
log.info("The Test status : ${teststatus}")
//return test[0].getResolution()
if (test[0].getResolution() != null){
    testresoultion = test[0].getResolution().name

}


def tran(ApplicationUser user, Issue issue, int actionnumber) {
    // transition an issue
    def workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)
    workflowTransitionUtil.setIssue(issue as MutableIssue)
    workflowTransitionUtil.setUserkey(user.key)
    workflowTransitionUtil.setAction(actionnumber)
    // validate and transition issue
    workflowTransitionUtil.validate()
    workflowTransitionUtil.progress()
    log.info("Methoda listener - Epic - " + issue + "Moved with action " + actionnumber)

}






//New  if Statement Made by Michael 18/07/2018
if(StoriesStatus.isEmpty()) {
    if(issue.issueType.name == "Epic" && issue.status.name == "QA" && teststatus == "Done") {
        //Set issue Resolution
        issue.setResolution(resolutionManager.getResolutionByName("Tested"))
        //Move Epic to Done
        tran(user, issue, 51)
        log.error("issue moved to Done By Michael")
        issue.store()
    }
    return
}



def StoriesResoultion = []
Stories.each {
    if (it.getResolution() != null) {
        StoriesResoultion.add(it.getResolution().name)

    }
}


if (issue.issueType.name == "Epic") {
    Thread.start {
        Thread.sleep(2000)
        status = issue.status.name
        log.debug("status" + status)
        // If all stories in status open && test != status open  && epic in status open epic moves to in progress.
        //Relevant platforms is requeired
        def flag = true
        StoriesStatus.each {

            if (it != "Open") {
                flag = false
            }
        }
        if (flag && teststatus != "Open" && status == "Open") {
            tran(user, issue, 21)
            log.error("If all stories in status open && test != status open  && epic in status open epic moves to in progress.")
        }

        // If at least one story in status in progress && epic in status open/ready to submit/in progress epic moves to in progress.
        // Original estimate and components are required
        if (StoriesStatus.contains("In Progress") && status == "Open") {
            tran(user, issue, 21)
            log.error("If at least one story in status in progress && epic in status open/ready to submit/in progress epic moves to in progress.")
        }
        if (StoriesStatus.contains("In Progress") && status == "Ready To Submit") {
            tran(user, issue, 171)
            log.error("If at least one story in status in progress && epic in status open/ready to submit/in progress epic moves to in progress.")
        }
        // If all stories in status ready to submit && epic in status open/ready to submit/in progress/reopened/hold epic moves to in progress.
        flag = true
        StoriesStatus.each {
            if (it != "Ready To Submit") {
                flag = false
            }
        }
        if (flag && status == "Open") {
            tran(user, issue, 21)
            log.error("If all stories in status ready to submit && epic in status open/ready to submit/in progress/reopened/hold epic moves to in progress.")
        }
        if (flag && (status == "HOLD" || status == "Reopened")) {
            tran(user, issue, 61)
            log.error("If all stories in status ready to submit && epic in status open/ready to submit/in progress/reopened/hold epic moves to in progress.")
        }
        if (flag && (status == "Ready To Submit")) {
            tran(user, issue, 171)
            log.error("If all stories in status ready to submit && epic in status open/ready to submit/in progress/reopened/hold epic moves to in progress.")
        }
        // If 0 stories in status in progress && at least one story in status ready to submit/hold/Done/reopened && epic in status open/ready to submit/in progress epic moves to in progress.
        flag = false
        StoriesStatus.each {
            if (it == "Ready To Submit" || it == "HOLD" || it == "Done" || it == "Reopened") {
                flag = true
            }
        }
        if (!StoriesStatus.contains("In Progress") && flag) {
            if (status == "Open") {
                tran(user, issue, 21)
                log.error("If 0 stories in status in progress && at least one story in status ready to submit/hold/Done/reopened && epic in status open/ready to submit/in progress epic moves to in progress.")
            }
            if (status == "Ready To Submit") {
                tran(user, issue, 171)
                log.error("If 0 stories in status in progress && at least one story in status ready to submit/hold/Done/reopened && epic in status open/ready to submit/in progress epic moves to in progress.")
            }

        }

        // If all stories in status hold and test in status open/test plan/execution && epic in status open/ready to submit/in progress epic moves to in progress
        flag = true
        StoriesStatus.each {
            if (it != "HOLD") {
                flag = false
            }
        }
        if (flag && (teststatus == "Open" || teststatus == "Writing test plan" || teststatus == "Execution") && status == "Open") {
            tran(user, issue, 21)
            log.error("If all stories in status hold and test in status open/test plan/execution && epic in status open/ready to submit/in progress epic moves to in progress")
        }
        if (flag && (teststatus == "Open" || teststatus == "Writing test plan" || teststatus == "Execution") && status == "Ready To Submit") {
            tran(user, issue, 171)
            log.error("If all stories in status hold and test in status open/test plan/execution && epic in status open/ready to submit/in progress epic moves to in progress")
        }
        // If at least one story in status open && one story in status Done && epic in status open/ready to submit/in progress epic moves to in progress.
        if (StoriesStatus.contains("Open") && StoriesStatus.contains("Done") && status == "Open") {
            tran(user, issue, 21)
            log.error("If at least one story in status open && one story in status Done && epic in status open/ready to submit/in progress epic moves to in progress.")
        }
        if (StoriesStatus.contains("Open") && StoriesStatus.contains("Done") && status == "Ready To Submit") {
            tran(user, issue, 171)
            log.error("If at least one story in status open && one story in status Done && epic in status open/ready to submit/in progress epic moves to in progress.")
        }
        // If 0 stories in status in progress && at least one story in status ready to submit/hold/Done/reopened && epic in status Done epic move to reopened
        // modified Michael & Nati 11/7/18 - Done should not be checked
        //if(!StoriesStatus.contains("In Progress") && (StoriesStatus.contains("Ready To Submit") || StoriesStatus.contains("HOLD") || StoriesStatus.contains("Done") || StoriesStatus.contains("Reopened"))){
        if (!StoriesStatus.contains("In Progress") && (StoriesStatus.contains("Ready To Submit") || StoriesStatus.contains("HOLD") || StoriesStatus.contains("Reopened"))) {
            if (status == "Done") {
                tran(user, issue, 151)
                log.error("If 0 stories in status in progress && at least one story in status ready to submit/hold/Done/reopened && epic in status Done epic move to reopened.")
            }
        }

        // If one of the story in status hold or open && epic in status Done epic move to reopened
        if ((StoriesStatus.contains("HOLD") || StoriesStatus.contains("Open")) && status == "Done") {
            tran(user, issue, 151)
            log.error("If one of the story in status hold or open && epic in status Done epic move to reopened")
        }

        // If all stories in status Done && the test in status open/test plan/execution && epic not in status Done epic moves to QA.
        flag = true
        StoriesStatus.each {
            if (it != "Done") {
                flag = false
            }
        }
        if (flag && (teststatus == "Open" || teststatus == "Writing test plan" || teststatus == "Execution")) {
            if (status != "Done") {
                if (status == "HOLD" || status == "Ready To Submit") {
                    tran(user, issue, 71)
                    log.error("If all stories in status Done && the test in status open/test plan/execution && epic not in status Done epic moves to QA.")
                }
                if (status == "In Progress") {
                    tran(user, issue, 31)
                    log.error("If all stories in status Done && the test in status open/test plan/execution && epic not in status Done epic moves to QA.")
                }
            }
        }

        // If all stories in status Done && the test in status hold  && epic not in status Done epic moves to Hold.
        flag = true
        StoriesStatus.each {
            if (it != "Done") {
                flag = false
            }
        }
        if (flag && teststatus == "HOLD" && status != "Done") {
            tran(user, issue, 11)
            log.error("If all stories in status Done && the test in status hold  && epic not in status Done epic moves to Hold.")
        }

        // If all stories in status Done && the test in status Done  epic moves to Done.
        flag = true
        StoriesStatus.each {
            if (it != "Done") {
                flag = false
            }
        }
        if (flag && teststatus == "Done") {
            if (status == "In Progress") {
                if (StoriesResoultion.contains("Tested") || testresoultion == "Tested") {
                    issue.setResolution(resolutionManager.getResolutionByName("Tested"))
                    issue.store()
                }
                if (!StoriesResoultion.contains("Tested") && testresoultion != "Tested" && StoriesResoultion.contains("Suspended") || testresoultion == "Suspended") {
                    issue.setResolution(resolutionManager.getResolutionByName("Suspended"))
                    issue.store()
                }
                if (!StoriesResoultion.contains("Tested") && testresoultion != "Tested" && !StoriesResoultion.contains("Suspended") || testresoultion != "Suspended" && !StoriesResoultion.contains("Rejected") || testresoultion == "Rejected") {
                    issue.setResolution(resolutionManager.getResolutionByName("Rejected"))
                    issue.store()
                }
                if (!StoriesResoultion.contains("Tested") && testresoultion != "Tested" && !StoriesResoultion.contains("Suspended") || testresoultion != "Suspended" && !StoriesResoultion.contains("Rejected") || testresoultion != "Rejected" && StoriesResoultion.contains("Duplicated") || testresoultion == "Duplicated") {
                    issue.setResolution(resolutionManager.getResolutionByName("Duplicated"))
                    issue.store()
                }
                if (!StoriesResoultion.contains("Tested") && testresoultion != "Tested" && !StoriesResoultion.contains("Suspended") || testresoultion != "Suspended" && !StoriesResoultion.contains("Rejected") || testresoultion != "Rejected" && !StoriesResoultion.contains("Duplicated") || testresoultion != "Duplicated" && StoriesResoultion.contains("Not testable") || testresoultion != "Not testable") {
                    issue.setResolution(resolutionManager.getResolutionByName("Tested"))
                    issue.store()
                }
                tran(user, issue, 91)
                log.error("If all stories in status Done && the test in status Done  epic moves to Done.")
            }
            if (status == "HOLD") {
                tran(user, issue, 81)
                log.error("If all stories in status Done && the test in status Done  epic moves to Done.")
            }
        }

        // If all stories in status reopened moves epic to reopened.
        flag = true
        StoriesStatus.each {
            if (it != "Reopened") {
                flag = false
            }
        }
        if (flag) {
            tran(user, issue, 151)
            log.error("If all stories in status reopened moves epic to reopened.")
        }

        // If all stories in status hold and test in status hold or done epic moves to Hold.
        flag = true
        StoriesStatus.each {
            if (it != "HOLD") {
                flag = false
            }
        }
        if (flag && (teststatus == "HOLD" || teststatus == "Done")) {
            tran(user, issue, 11)
            log.error("If all stories in status hold and test in status hold or done epic moves to Hold.")
        }

        // If all stories in status hold and test in status open/test plan/execution && epic in status open/ready to submit/in progress epic moves to in progress
        flag = true
        StoriesStatus.each {
            if (it != "HOLD") {
                flag = false
            }
        }
        if (flag && (teststatus == "Open" || teststatus == "Writing test plan" || teststatus == "Execution") && status == "Open") {
            tran(user, issue, 21)
            log.error(" If all stories in status hold and test in status open/test plan/execution && epic in status open/ready to submit/in progress epic moves to in progress")
        }
        if (flag && (teststatus == "Open" || teststatus == "Writing test plan" || teststatus == "Execution") && status == "Ready To Submit") {
            tran(user, issue, 171)
            log.error(" If all stories in status hold and test in status open/test plan/execution && epic in status open/ready to submit/in progress epic moves to in progress")
        }
        // If at least one story in status open && one story in status Done && epic in status open/ready to submit/in progress epic moves to in progress.
        if (StoriesStatus.contains("Open") && StoriesStatus.contains("Done") && status == "Open") {
            tran(user, issue, 21)
            log.error("If at least one story in status open && one story in status Done && epic in status open/ready to submit/in progress epic moves to in progress.")
        }
        if (StoriesStatus.contains("Open") && StoriesStatus.contains("Done") && status == "Ready To Submit") {
            tran(user, issue, 171)
            log.error("If at least one story in status open && one story in status Done && epic in status open/ready to submit/in progress epic moves to in progress.")
        }

        // If at least one story in status reopen && epic in status done epic moves to reopened.
        if (StoriesStatus.contains("Reopened") && status == "Done") {
            tran(user, issue, 151)
            log.error("If at least one story in status reopen && epic in status done epic moves to reopened.")
        }

        // If at least one story in status ready to submit && other stories in status hold or done && epic in status open/ready to submit/in progress/reopened epic moves to in progress
        flag = true
        StoriesStatus.each {
            if (it != "HOLD" && it != "Done" && it != "Ready To Submit") {
                flag = false
            }
        }
        if (flag && StoriesStatus.contains("Ready To Submit")) {
            if (status == "Open") {
                tran(user, issue, 21)
                log.error("If at least one story in status ready to submit && other stories in status hold or done && epic in status open/ready to submit/in progress/reopened epic moves to in progress")
            }
            if (status == "Reopened") {
                tran(user, issue, 61)
                log.error("If at least one story in status ready to submit && other stories in status hold or done && epic in status open/ready to submit/in progress/reopened epic moves to in progress")
            }
            if (status == "Ready To Submit") {
                tran(user, issue, 171)
                log.error("If at least one story in status ready to submit && other stories in status hold or done && epic in status open/ready to submit/in progress/reopened epic moves to in progress")
            }
        }

    }

}


