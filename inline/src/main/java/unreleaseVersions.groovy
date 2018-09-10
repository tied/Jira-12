package com.atlassian.jira.project.version

import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme
import com.atlassian.jira.project.Project
import org.apache.log4j.Logger
import org.apache.log4j.Level


log.setLevel(Level.DEBUG)
//Set issue
log.error("******************************************Script in running***********************************")
//def issue = ComponentAccessor.getIssueManager().getIssueObject("SMAR-903")
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def versionManager = ComponentAccessor.getVersionManager()
Collection <Version> allVersions = versionManager.getAllVersionsUnreleased(true)

//Get unreleased versions field
def unreleasedField = customFieldManager.getCustomFieldObject('customfield_11102')
Options options = ComponentAccessor.getOptionsManager().getOptions(unreleasedField.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig());
for (int i = 0; i< allVersions.size(); i++){



    if(!Arrays.asList(options).toString().contains(allVersions[i].name)){


        addOptionToCustomField(unreleasedField,allVersions[i].name)
    }

}

public Option addOptionToCustomField(CustomField customField, String value) {
    Option newOption = null;
    if (customField != null) {
        List<FieldConfigScheme> schemes = customField.getConfigurationSchemes();
        if (schemes != null && !schemes.isEmpty()) {
            FieldConfigScheme sc = schemes.get(0);
            Map configs = sc.getConfigsByConfig();
            if (configs != null && !configs.isEmpty()) {
                FieldConfig config = (FieldConfig) configs.keySet().iterator().next();
                OptionsManager optionsManager = ComponentAccessor.getOptionsManager();
                Options l = optionsManager.getOptions(config);
                int nextSequence = l.isEmpty() ? 1 : l.getRootOptions().size() + 1;
                newOption = optionsManager.createOption(config, null, (long) nextSequence, value);

            }
        }
    }

    return newOption
}



