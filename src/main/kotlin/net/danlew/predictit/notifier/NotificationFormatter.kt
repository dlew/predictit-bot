package net.danlew.predictit.notifier

import net.danlew.predictit.db.Database
import net.danlew.predictit.model.FormattedNotification
import net.danlew.predictit.model.Notification
import net.danlew.predictit.model.Notification.Type.*

class NotificationFormatter(val db: Database) {

  fun formatNotification(notification: Notification): FormattedNotification {
    return when (notification.type) {
      MARKET_OPENED -> marketOpened(notification)
      MARKET_CLOSED -> marketClosed(notification)
      CONTRACT_ADDED -> contractAdded(notification)
      HIGH_VOLATILITY -> highVolatility(notification)
      NEGATIVE_RISK -> negativeRisk(notification)
    }
  }

  private fun marketOpened(notification: Notification): FormattedNotification {
    require(notification.type == MARKET_OPENED)

    val market = db.getMarket(notification.marketId)!!

    return FormattedNotification(
      text = "New market! ${market.name}",
      textShort = "New market! ${market.shortName}",
      link = market.url,
      imageUrl = market.image
    )
  }

  private fun marketClosed(notification: Notification): FormattedNotification {
    require(notification.type == MARKET_CLOSED)

    val market = db.getMarket(notification.marketId)!!

    return FormattedNotification(
      text = "Market CLOSED! ${market.name}",
      textShort = "Market CLOSED! ${market.shortName}",
      link = market.url,
      imageUrl = market.image
    )
  }

  private fun contractAdded(notification: Notification): FormattedNotification {
    require(notification.type == CONTRACT_ADDED)
    requireNotNull(notification.contractId)

    val market = db.getMarket(notification.marketId)!!
    val contract = db.getContract(notification.contractId)!!

    return FormattedNotification(
      text = "New contract in ${market.name}: ${contract.name}",
      textShort = "New contract in ${market.shortName}: ${contract.shortName}",
      link = market.url,
      imageUrl = contract.image
    )
  }

  private fun highVolatility(notification: Notification): FormattedNotification {
    require(notification.type == HIGH_VOLATILITY)

    val market = db.getMarket(notification.marketId)!!

    return FormattedNotification(
      text = "High volatility in: ${market.name}",
      textShort = "High volatility in: ${market.shortName}",
      link = market.url,
      imageUrl = market.image
    )
  }

  private fun negativeRisk(notification: Notification): FormattedNotification {
    require(notification.type == NEGATIVE_RISK)

    val market = db.getMarket(notification.marketId)!!

    return FormattedNotification(
      text = "Negative risk opportunity: ${market.name}",
      textShort = "Negative risk opportunity: ${market.shortName}",
      link = market.url,
      imageUrl = market.image
    )
  }

}