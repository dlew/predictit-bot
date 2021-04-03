package net.danlew.predictit.notifier

import net.danlew.predictit.model.FormattedNotification
import twitter4j.StatusUpdate
import twitter4j.TwitterFactory


class TwitterNotifier : Notifier {

  private val twitter = TwitterFactory.getSingleton()

  // TODO: We should refresh this once/day
  private val apiConfiguration = twitter.apiConfiguration

  override fun notify(notifications: Set<FormattedNotification>) {
    notifications.forEach {
      tweet(it)

      // Wait one second between tweeting just to be kind
      Thread.sleep(1000)
    }
  }

  private fun tweet(notification: FormattedNotification) {
    val statusUpdate = StatusUpdate(formatStatus(notification))
    twitter.updateStatus(statusUpdate)
  }

  private fun formatStatus(notification: FormattedNotification): String {
    val status = "${notification.text}\n\n"
    val shortStatus = "${notification.textShort}\n\n"
    val urlLength = apiConfiguration.shortURLLengthHttps

    if (status.length + urlLength < CHARACTER_LIMIT) {
      return status + notification.link
    }
    else {
      return shortStatus + notification.link
    }
  }

  private companion object {
    private const val CHARACTER_LIMIT = 280
  }
}