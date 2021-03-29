package net.danlew.predictit

import net.danlew.predictit.analyzer.MarketAnalyzer
import net.danlew.predictit.api.PredictItApi
import net.danlew.predictit.db.Database
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

  fun run() {
    // Refresh the data
    val marketsWithPrices = api.allMarkets() ?: return

    // TODO: Detect when a Market is closed and clean up DB/Notifications

    // Update the DB
    db.insertOrUpdate(marketsWithPrices)

    // Run every Market through every Analyzer
    val notifications = db.allMarkets().flatMap { market ->
      val priceHistory = db.priceHistory(market.id)
      analyzers.flatMap { analyzer ->
        analyzer.analyze(market, priceHistory)
      }
    }.toSet()

    // Find out which notifications are new
    val newNotifications = notifications - db.allNotifications()
    db.insertOrUpdateNotifications(newNotifications)

    // Notify everyone of new notifications
    notifiers.forEach { it.notify(newNotifications) }

    // TODO: Cull old data so the DB doesn't get overly large?
  }

}