package net.danlew.predictit.notifier

import net.danlew.predictit.model.Notification
import org.slf4j.LoggerFactory

object LogNotifier : Notifier {

  private val logger = LoggerFactory.getLogger(LogNotifier::class.java)

  override fun notify(notifications: Set<Notification>) {
    notifications.forEach {
      logger.info(it.format())
    }
  }

}