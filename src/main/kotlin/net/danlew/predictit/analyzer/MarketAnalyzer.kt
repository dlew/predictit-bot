package net.danlew.predictit.analyzer

import net.danlew.predictit.model.Market
import net.danlew.predictit.model.MarketWithPrices
import net.danlew.predictit.model.Notification

interface MarketAnalyzer {

  /**
   * Analyzes a [Market]'s price history then returns any [Notification]s that may result.
   */
  fun analyze(oldMarketData: Market?, latestData: MarketWithPrices): Set<Notification>

}