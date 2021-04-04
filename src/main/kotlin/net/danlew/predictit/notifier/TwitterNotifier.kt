package net.danlew.predictit.notifier

import net.danlew.predictit.model.FormattedNotification
import org.slf4j.LoggerFactory
import twitter4j.StatusUpdate
import twitter4j.TwitterAPIConfiguration
import twitter4j.TwitterFactory


class TwitterNotifier : Notifier {

  private val logger = LoggerFactory.getLogger(TwitterNotifier::class.java)

  private val twitter = TwitterFactory.getSingleton()

  // TODO: We should refresh this once/day
  private var apiConfiguration: TwitterAPIConfiguration? = null

  init {
    // We use "can hit Configuration endpoint" as a test for whether the auth credentials worked
    try {
      apiConfiguration = twitter.apiConfiguration
    } catch (e: Exception) {
      logger.error("Twitter4J is not configured properly; not tweeting output.", e)
    }
  }

  override fun notify(notifications: Set<FormattedNotification>) {
    if (apiConfiguration == null) {
      return
    }

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
    val urlLength = apiConfiguration?.shortURLLengthHttps ?: 25

    if (status.length + urlLength < CHARACTER_LIMIT) {
      return status + notification.link
    } else {
      return shortStatus + notification.link
    }
  }

  private companion object {
    private const val CHARACTER_LIMIT = 280
  }
}