package net.danlew.predictit.analyzer

import net.danlew.predictit.db.Database
import net.danlew.predictit.model.*
import java.time.Instant

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
class NegativeRiskAnalyzer(
  private val db: Database,

  // Define how long negative risk has to be present before we alert
  private val forAtLeastSeconds: Long = 60 * 5 // Default to 5 minutes
) : MarketAnalyzer {

  init {
    require(forAtLeastSeconds >= 0)
  }

  override fun analyze(oldMarketData: Market?, latestData: MarketWithPrices): Set<Notification> {
    // Closed markets can't be used!
    if (latestData.market.status == MarketStatus.CLOSED) return emptySet()

    // Check both latest prices *and* all historical prices in the last N seconds have negative risk
    if (latestData.pricesAtTime.prices.hasNegativeRisk()
      && db.pricesSince(latestData.market.id, cutoff()).all { it.prices.hasNegativeRisk() }) {
      return setOf(
        Notification(
          marketId = latestData.market.id,
          type = Notification.Type.NEGATIVE_RISK
        )
      )
    }

    return emptySet()
  }

  private fun cutoff() = Instant.now().minusSeconds(forAtLeastSeconds)

  private fun Map<ContractId, Price>.hasNegativeRisk(): Boolean {
    val totalPrices = values
      .map { it.value }
      .filter { it > 1 }
      .sum()

    // We specifically choose GREATER THAN $1.10 because $1.10 exactly means you'd only break even!
    return totalPrices > 110
  }

}