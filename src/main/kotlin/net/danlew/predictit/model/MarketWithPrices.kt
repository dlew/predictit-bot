package net.danlew.predictit.model

data class MarketWithPrices(
  val market: Market,
  val prices: Map<ContractId, PriceAtTime>
) {

  init {
    require(market.contracts.map { it.id }.toSet() == prices.keys) {
      "Price required for every contract"
    }
  }

}
