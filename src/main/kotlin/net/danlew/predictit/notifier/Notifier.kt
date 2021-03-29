package net.danlew.predictit.notifier

import net.danlew.predictit.model.Notification

interface Notifier {

  fun notify(notifications: Set<Notification>)

}