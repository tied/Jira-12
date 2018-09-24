package console
/**
 * Created by michaelsh on 7/19/2018.
 */



import com.atlassian.crowd.embedded.api.CrowdService
import com.atlassian.crowd.embedded.api.UserWithAttributes
import com.atlassian.crowd.embedded.impl.ImmutableUser
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.ApplicationUsers
import com.atlassian.jira.user.util.UserUtil

int numOfDays = 100 // Number of days the user was not logged in
Date dateLimit = (new Date())- numOfDays

UserUtil userUtil = ComponentAccessor.userUtil
CrowdService crowdService = ComponentAccessor.crowdService
UserService userService = ComponentAccessor.getComponent(UserService)
ApplicationUser updateUser
UserService.UpdateUserValidationResult updateUserValidationResult

long count = 0

userUtil.getUsers().findAll{it.isActive()}.each {
    UserWithAttributes user = crowdService.getUserWithAttributes(it.getName())
    String lastLoginMillis = user.getValue('login.lastLoginMillis')
    if (lastLoginMillis?.isNumber()) {
        Date d = new Date(Long.parseLong(lastLoginMillis))
        if (d.before(dateLimit)) {
            updateUser = ApplicationUsers.from(ImmutableUser.newUser(user).active(false).toUser())
            updateUserValidationResult = userService.validateUpdateUser(updateUser)
            if (updateUserValidationResult.isValid()) {
                userService.updateUser(updateUserValidationResult)
                log.info "Deactivated ${updateUser.name}"
                count++
            } else {
                log.error "Update of ${user.name} failed: ${updateUserValidationResult.getErrorCollection().getErrors().entrySet().join(',')}"
            }
        }
    }
}

"${count} users deactivated.\n"