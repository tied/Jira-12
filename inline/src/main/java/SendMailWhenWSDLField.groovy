package com.atlassian.mail.server
import com.atlassian.jira.component.ComponentAccessor
import groovy.json.JsonSlurper
import java.nio.charset.StandardCharsets
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.mail.Email

def log = Logger.getLogger("com.methoda.WSDL")
log.setLevel(Level.DEBUG)
//def issue = ComponentAccessor.getIssueManager().getIssueObject("IGS-864")
def issue = event.issue
def user = event.user
//log.error("*******************************************WSDL Script start - "+issue+"***********************************")
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
def TOB = customFieldManager.getCustomFieldObject("customfield_10107")
def TOBval = TOB.getValue(issue) as String
def srtaskid = customFieldManager.getCustomFieldObject("customfield_10601").getValue(issue)

if(TOBval =="FieldBug" && srtaskid && user!="oracle_sc"){
    log.error("*******************************************WSDL Script start - "+issue+"***********************************")

    def baseurl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl")
    def userName = "michaelsh";
    def userpass = "gfoe7186";
    def issuekey = issue.key
    def reporter = issue.getReporter()
    def assignee = issue.getAssignee()
    def summary = issue.getSummary()
    def priority = issue.getPriority()
    def resolution = issue.getResolution()
    def project = issue.getProject()
    //if(!description){description=""}else{description=issue.getDescription()}
    if(!reporter){reporter=""}else{reporter=reporter.name}
    if(!priority){priority=""}else{priority=priority.name}
    if(!assignee){assignee=""}else{assignee=assignee.name}
    if(!resolution){resolution=""}else{resolution=resolution.name}
    if(!project){project=""}else{project=project.name}
    def SRNumber = customFieldManager.getCustomFieldObject("customfield_10509")
    def Customer = customFieldManager.getCustomFieldObject("customfield_10226")
    def RelevantPlatforms = customFieldManager.getCustomFieldObject("customfield_10113")
    def SRTaskid = customFieldManager.getCustomFieldObject("customfield_10601")
    def Solution = customFieldManager.getCustomFieldObject("customfield_10505")
    def Workaround = customFieldManager.getCustomFieldObject("customfield_10108")
    def waitingfor = customFieldManager.getCustomFieldObject("customfield_10117")
    def reasonofclosing = customFieldManager.getCustomFieldObject("customfield_10237")
    def responsibleteam = customFieldManager.getCustomFieldObject("customfield_10302")
    def fixedinbuild = customFieldManager.getCustomFieldObject("customfield_10303")
    if(!SRNumber.getValue(issue)){SRNumber = ""}else{SRNumber = SRNumber.getValue(issue)}
    if(!Customer.getValue(issue)){Customer = ""}else{Customer = Customer.getValue(issue)}
    def FoundInBuild = customFieldManager.getCustomFieldObject("customfield_10114").getValue(issue) //mandatory field
    if(!SRTaskid.getValue(issue)){SRTaskid = ""}else{SRTaskid = SRTaskid.getValue(issue)}
    if(!RelevantPlatforms.getValue(issue)){RelevantPlatforms = ""}else{RelevantPlatforms = RelevantPlatforms.getValue(issue)}
    if(!Solution.getValue(issue)){Solution = ""}else{Solution = Solution.getValue(issue)}
    if(!Workaround.getValue(issue)){Workaround = ""}else{Workaround = Workaround.getValue(issue)}
    if(!waitingfor.getValue(issue)){waitingfor = ""}else{waitingfor = waitingfor.getValue(issue)}
    if(!reasonofclosing.getValue(issue)){reasonofclosing = ""}else{reasonofclosing = reasonofclosing.getValue(issue)}
    if(!fixedinbuild.getValue(issue)){fixedinbuild = ""}else{fixedinbuild = fixedinbuild.getValue(issue)}
    // if(!responsibleteam.getValue(issue)){responsibleteam = ""}else{responsibleteam = responsibleteam.getValue(issue).name[0]}

//def effectesversions = origJsonObject.fields.versions.name // array
    def fixversions = issue.getFixVersions() // array
    def fixversionscut = ""
    def releasedate = "("
    if(fixversions){
        for(int i = 0; i<fixversions.size();i++){
            fixversionscut = fixversionscut + fixversions[i].name + ","
            releasedate = releasedate + fixversions[i].name + "-" + fixversions[i].releaseDate + "),("
        }
        fixversionscut = fixversionscut.substring(0, fixversionscut.length() - 1)
        releasedate = releasedate.substring(0, releasedate.length() - 2)
        releasedate = releasedate.replace("-null", "")
    }else{
        fixversionscut = ""
        releasedate = ""
    }

    def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
    def issueChanges = changeHistoryManager.getAllChangeItems(issue)
    //get status history
    def statushistory = []
    issueChanges.each { change ->
        if (change.getField() in ["status"]){
            statushistory.add(change.getTos())
        }
    }

    def status
    if(statushistory.size()>1){
        status =  statushistory[statushistory.size()-1].toString()
        status = status.replaceAll("[^a-z.A-Z ]", "")
    }else{
        status =  issue.getStatus (  ).name

    }

    //get resulution history
    def resulutionhistory = []
    issueChanges.each { change ->
        if (change.getField() in ["resolution"]){
            resulutionhistory.add(change.getTos())
        }
    }
    if(resolution){
        resolution =  resulutionhistory[resulutionhistory.size()-1].toString()
        resolution = resolution.substring(7,resolution.size()-1)
    }else{
        resolution = ""
    }
    //get waitingfor history
    def waitingforhistory = []
    issueChanges.each { change ->
        if (change.getField() in ["Waiting for"]){
            waitingforhistory.add(change.getTos())
        }
    }
    if(waitingfor){
        waitingfor =  waitingforhistory[waitingforhistory.size()-1].toString()
        waitingfor = waitingfor.substring(7,waitingfor.size()-1)
    }else{
        waitingfor = ""
    }
    log.error("summary - " + summary + ", IssueKey" + issuekey)
//*********************************************************************************
    log.error("*******************************************Start define massage ***********************************")
    def url1 = baseurl + '/rest/scriptrunner/latest/custom/postxml'
    def userName1 = "michaelsh";
    def userpass1 = "gfoe7186";
    def body = """
{"xml":"<soap:Envelope xmlns:soap=\\"http://schemas.xmlsoap.org/soap/envelope/\\">
    <soap:Body>
        <ns1:process xmlns:ns1=\\"http://xmlns.oracle.com/JIRA2ORACLEDB/JIRA2ORACLEDB/MainProcess\\">
            <ns1:issuekey>${issuekey}</ns1:issuekey>
            <ns1:status>${status}</ns1:status>
            <ns1:waiting_for>${waitingfor}</ns1:waiting_for>
            <ns1:resolution>${resolution}</ns1:resolution>
            <ns1:priority>${priority}</ns1:priority>
            <ns1:sr_task_id>${SRTaskid}</ns1:sr_task_id>
            <ns1:summary></ns1:summary>
            <ns1:project></ns1:project>
            <ns1:affected_version></ns1:affected_version>
            <ns1:fixversion>${fixversionscut}</ns1:fixversion>
            <ns1:release_date>${releasedate}</ns1:release_date>
            <ns1:reporter>${reporter}</ns1:reporter>
            <ns1:assignee>${assignee}</ns1:assignee>
            <ns1:found_in_build>${FoundInBuild}</ns1:found_in_build>
            <ns1:customer>${Customer}</ns1:customer>
            <ns1:relevant_platform></ns1:relevant_platform>
            <ns1:solution>${Solution}</ns1:solution>
            <ns1:workaround>${Workaround}</ns1:workaround>
            <ns1:Field_bug_reason_of_closing>${reasonofclosing}</ns1:Field_bug_reason_of_closing>
            <ns1:Fixed_in_build>${fixedinbuild}</ns1:Fixed_in_build>
            <ns1:Jir_group>${responsibleteam}</ns1:Jir_group>
            <ns1:jir_exp_release_ver></ns1:jir_exp_release_ver>
            <ns1:future_use1></ns1:future_use1>
            <ns1:future_use2></ns1:future_use2>
            <ns1:future_use3></ns1:future_use3>
            <ns1:future_use4></ns1:future_use4>
            <ns1:future_use5></ns1:future_use5>
            <ns1:Documents>
                <ns1:Line>
                    <ns1:Comments></ns1:Comments>
                    <ns1:comment_timestamp></ns1:comment_timestamp>
                </ns1:Line>
            </ns1:Documents>
        </ns1:process>
    </soap:Body>
</soap:Envelope>"}
"""
    log.error("*******************************************Finish massage body***********************************")
    log.error("*******************************************Run Api1***********************************")
    String resp1 = runApi(url1,userName1,userpass1,"POST",body as String);
    log.error "WSDL response - " + resp1
//def jsonSlurper1 = new JsonSlurper()
//def origJsonObject1 = jsonSlurper.parseText(resp)
}

log.error("*******************************************Finish Api1***********************************")
log.error("*******************************************Run Api2***********************************")
String runApi(String url1, String userName, String userPass, String method, String body){
    URL jiraURL;
    String resp ="";
    try {
        jiraURL= new URL(url1) ;
        HttpURLConnection connection = (HttpURLConnection)jiraURL.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        String userCredentials = userName+":"+userPass;
        String basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString(userCredentials.getBytes());
        connection.setRequestProperty ("Authorization", basicAuth);
        connection.connect();
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(),StandardCharsets.UTF_8);
        wr.write(body);
        wr.flush();
        wr.close();
        Reader in1 = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        for (int c; (c = in1.read()) >= 0;/* System.out.print((char)c)*/) {
            resp += (char) c;
        }
    } catch (Exception e) {
        e.printStackTrace()

        sendEmail('michael.shechter@audiocodes.com','Error with service cloud integration',e.printStackTrace())
        return e
    }
    return resp;
}
log.error("*******************************************Finish Api2***********************************")
log.error("******************************************end***************************************")


String sendEmail(String emailAddr, String subject, String body) {
    SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
    if (mailServer) {
        Email email = new Email(emailAddr)
        email.setSubject(subject)
        email.setBody(body)
        mailServer.send(email)
        log.debug("Mail sent")
    } else {
        log.warn("Please make sure that a valid mailServer is configured")
    }
}