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
    db.insertOrUpdateNotifications(newNotifications)

    // Notify everyone of new notifications
    val formattedNotifications = newNotifications.map(notificationFormatter::formatNotification).toSet()
    notifiers.forEach { it.notify(formattedNotifications) }

    // TODO: Cull old data so the DB doesn't get overly large?
  }

  private fun getFreshMarketData(existingMarketIds: Set<MarketId>): Set<MarketWithPrices>? {
    // Refresh the data
    val marketsWithPrices = api.allMarkets() ?: return null

    // Manually refresh any markets not in "all markets" (this indicates a closed market)
    val missingMarketIds = marketsWithPrices.map { it.market.id } - existingMarketIds
    val missingMarkets = missingMarketIds.mapNotNull(api::marketById)

    return marketsWithPrices + missingMarkets
  }

}