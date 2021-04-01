package net.danlew.predictit.notifier

import net.danlew.predictit.model.FormattedNotification
import org.slf4j.LoggerFactory

object LogNotifier : Notifier {

  private val logger = LoggerFactory.getLogger(LogNotifier::class.java)

  override fun notify(notifications: Set<FormattedNotification>) {
    notifications.forEach {
      logger.info("${it.text} / ${it.link}")
    }
  }

}