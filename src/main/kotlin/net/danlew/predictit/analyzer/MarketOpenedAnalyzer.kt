package net.danlew.predictit.analyzer

import net.danlew.predictit.model.Market
import net.danlew.predictit.model.MarketStatus
import net.danlew.predictit.model.MarketWithPrices
import net.danlew.predictit.model.Notification

object MarketOpenedAnalyzer : MarketAnalyzer {

  override fun analyze(oldMarketData: Market?, latestData: MarketWithPrices): Set<Notification> {
    if (latestData.market.status == MarketStatus.OPEN
      && (oldMarketData == null || oldMarketData.status == MarketStatus.CLOSED)) {
      return setOf(Notification(latestData.market.id, type = Notification.Type.MARKET_OPENED))
    }

    return emptySet()
  }

}