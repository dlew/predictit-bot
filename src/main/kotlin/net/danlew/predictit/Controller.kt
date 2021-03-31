package net.danlew.predictit

import net.danlew.predictit.analyzer.MarketAnalyzer
import net.danlew.predictit.api.PredictItApi
import net.danlew.predictit.db.Database
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
    val oldMarkets = db.allMarkets()
    val oldMarketsById = oldMarkets.associateBy { it.id }

    // Refresh the data
    val marketsWithPrices = api.allMarkets() ?: return

    // TODO: Detect when a Market is closed and clean up DB/Notifications

    // Update the DB
    db.insertOrUpdate(marketsWithPrices)

    // Run every Market through every Analyzer
    val notifications = marketsWithPrices.flatMap { marketWithPrices ->
      analyzers.flatMap { analyzer ->
        analyzer.analyze(oldMarketsById[marketWithPrices.market.id], marketWithPrices)
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

}