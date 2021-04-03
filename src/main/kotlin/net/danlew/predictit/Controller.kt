package net.danlew.predictit

import net.danlew.predictit.analyzer.MarketAnalyzer
import net.danlew.predictit.api.PredictItApi
import net.danlew.predictit.db.Database
import net.danlew.predictit.model.MarketId
import net.danlew.predictit.model.MarketStatus
import net.danlew.predictit.model.MarketWithPrices
import net.danlew.predictit.notifier.NotificationFormatter
import net.danlew.predictit.notifier.Notifier

/**
 * Controls the flow of data through the app
 */
class Controller(
  private val api: PredictItApi,
  private val db: Database,
  private val analyzers: Set<MarketAnalyzer>,
  private val notifiers: Set<Notifier>
) {

  private val notificationFormatter = NotificationFormatter(db)

  fun run() {
    val oldMarketData = db.allMarkets(MarketStatus.OPEN).associateBy { it.id }

    // Refresh the data
    val newMarketData = getFreshMarketData(oldMarketData.keys) ?: return

    // Update the DB
    db.insertOrUpdate(newMarketData)

    // Run every Market through every Analyzer
    val notifications = newMarketData.flatMap { marketWithPrices ->
      analyzers.flatMap { analyzer ->
        analyzer.analyze(oldMarketData[marketWithPrices.market.id], marketWithPrices)
      }
    }.toSet()

    // Find out which notifications are new
    val newNotifications = notifications - db.allNotifications()

    // Update notifications; this may replace old Notifications with new Notifications
    // with a more recent expiration date
    db.insertOrUpdateNotifications(notifications)

    // Notify everyone of new notifications
    val formattedNotifications = newNotifications.map(notificationFormatter::formatNotification).toSet()
    notifiers.forEach { it.notify(formattedNotifications) }

    // Delete expired notifications; this allows us to re-notify about events that may
    // have happened in the past, stopped, then started again (e.g. negative risk on May 1,
    // no negative risk on May 3, and negative risk again on May 5).
    db.deleteExpiredNotifications()
  }

  private fun getFreshMarketData(existingMarketIds: Set<MarketId>): Set<MarketWithPrices>? {
    // Refresh the data
    val marketsWithPrices = api.allMarkets() ?: return null

    // Manually refresh any markets not in "all markets" (this indicates a closed market)
    val missingMarketIds = existingMarketIds - marketsWithPrices.map { it.market.id }
    val missingMarkets = missingMarketIds.mapNotNull(api::marketById)

    return marketsWithPrices + missingMarkets
  }

}