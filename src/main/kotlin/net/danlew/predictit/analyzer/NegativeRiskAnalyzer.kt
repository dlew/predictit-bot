package net.danlew.predictit.analyzer

import net.danlew.predictit.model.Market
import net.danlew.predictit.model.MarketStatus
import net.danlew.predictit.model.MarketWithPrices
import net.danlew.predictit.model.Notification

/**
 * Detects when a [Market] is ripe for negative risk.
 *
 * Negative risk is achieved when buying all the YESes adds up to $1.10
 * (it's $1.10 because of PredictIt fees of 10% of earnings).
 *
 * It does not do a complicated analysis; technically negative risk
 * can be achieved even without all contracts adding up to $1.10, but
 * you have to do weird stuff that is hard to explain in a simple
 * [Notification].
 */
object NegativeRiskAnalyzer : MarketAnalyzer {

  override fun analyze(oldMarketData: Market?, latestData: MarketWithPrices): Set<Notification> {
    // Closed markets can't be used!
    if (latestData.market.status == MarketStatus.CLOSED) return emptySet()

    val totalPrices = latestData.pricesAtTime.prices.values
      .map { it.value }
      .filter { it > 1 }
      .sum()

    // We specifically choose GREATER THAN $1.10 because $1.10 exactly means you'd only break even!
    if (totalPrices > 110) {
      return setOf(
        Notification(
          marketId = latestData.market.id,
          type = Notification.Type.NEGATIVE_RISK
        )
      )
    }

    return emptySet()
  }

}