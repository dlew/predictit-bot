package net.danlew.predictit.api.model

import net.danlew.predictit.model.ContractId
import net.danlew.predictit.model.Market
import net.danlew.predictit.model.ContractPrice

data class MarketWithPrices(
  val market: Market,
  val lastTradePrices: Map<ContractId, ContractPrice>
) {

  init {
    require(market.contracts.map { it.id }.toSet() == lastTradePrices.keys) {
      "Price required for every contract"
    }
  }

}
