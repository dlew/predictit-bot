package net.danlew.predictit.notifier

import net.danlew.predictit.db.Database
import net.danlew.predictit.model.FormattedNotification
import net.danlew.predictit.model.Notification

class NotificationFormatter(val db: Database) {

  fun formatNotification(notification: Notification): FormattedNotification {
    // TODO: Obviously this needs to be better
    return FormattedNotification(notification.toString())
  }

}