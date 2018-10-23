package me.scana.okgradle.util

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import me.scana.okgradle.Copys

class Notifier(private val project: Project) {

    private val notificationGroup = NotificationGroup(
            Copys.TITLE,
            NotificationDisplayType.BALLOON,
            true
    )

    fun showDependenciesAddedMessage(module: String?, dependencies: List<String>) {
        showMessage(Copys.DEPENDENCY_ADDED_TITLE.format(module), dependencies.joinToString("\n"))
    }

    fun showDependenciesStatementCopiedMessage() {
        showMessage(Copys.DEPENDENCY_COPIED_TITLE, Copys.DEPENDENCY_COPIED_MSG)
    }

    private fun showMessage(title: String, message: String) {
        val notification = notificationGroup.createNotification(title, null, message, NotificationType.INFORMATION)
        ApplicationManager.getApplication().invokeLater { Notifications.Bus.notify(notification, project) }
    }

}
