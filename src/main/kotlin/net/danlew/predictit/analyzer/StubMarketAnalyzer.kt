package net.danlew.predictit.analyzer

import net.danlew.predictit.model.ContractId
import net.danlew.predictit.model.Market
import net.danlew.predictit.model.Notification
import net.danlew.predictit.model.PriceAtTime

/**
 * Stb [MarketAnalyzer] for dev purposes.
 */
class StubMarketAnalyzer(private val type: Notification.Type) : MarketAnalyzer {

  override fun analyze(market: Market, priceHistory: Map<ContractId, List<PriceAtTime>>): Set<Notification> {
    return setOf(Notification(market, type))
  }

}