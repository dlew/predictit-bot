package net.danlew.predictit.analyzer

import net.danlew.predictit.model.*

/**
 * Detects newly added [Contract]s.
 */
object ContractAddedAnalyzer : MarketAnalyzer {

  override fun analyze(oldMarketData: Market?, latestData: MarketWithPrices): Set<Notification> {
    // If this market just opened, we don't count any contracts as new
    if (oldMarketData == null || oldMarketData.status == MarketStatus.CLOSED) return emptySet()

    val oldContractIds = oldMarketData.contracts.map(Contract::id)
    val latestContractIds = latestData.market.contracts.map(Contract::id)

    val newContractIds = latestContractIds - oldContractIds

    return newContractIds.map {
      Notification(
        marketId = oldMarketData.id,
        contractId = it,
        type = Notification.Type.CONTRACT_ADDED
      )
    }.toSet()
  }

}