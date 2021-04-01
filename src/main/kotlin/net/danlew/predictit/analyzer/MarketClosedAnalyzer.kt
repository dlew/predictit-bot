package net.danlew.predictit.analyzer

import net.danlew.predictit.model.Market
import net.danlew.predictit.model.MarketStatus
import net.danlew.predictit.model.MarketWithPrices
import net.danlew.predictit.model.Notification

/**
 * Detects recently closed [Market]s.
 */
object MarketClosedAnalyzer : MarketAnalyzer {

  override fun analyze(oldMarketData: Market?, latestData: MarketWithPrices): Set<Notification> {
    if (latestData.market.status == MarketStatus.CLOSED && oldMarketData?.status == MarketStatus.OPEN) {
      return setOf(Notification(latestData.market.id, type = Notification.Type.MARKET_CLOSED))
    }

    return emptySet()
  }

}