package net.danlew.predictit.notifier

import net.danlew.predictit.model.FormattedNotification

interface Notifier {

  fun notify(notifications: Set<FormattedNotification>)

}