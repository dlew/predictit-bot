package net.danlew.predictit.analyzer

import net.danlew.predictit.model.ContractId
import net.danlew.predictit.model.PriceAtTime
import net.danlew.predictit.model.Market
import net.danlew.predictit.model.Notification

interface MarketAnalyzer {

  /**
   * Analyzes a [Market]'s price history then returns any [Notification]s that may result.
   *
   * You can provide as much or as little price history as you want; some analyzers may not
   * work as well with limited data, though.
   */
  fun analyze(market: Market, priceHistory: Map<ContractId, List<PriceAtTime>>): Set<Notification>

}