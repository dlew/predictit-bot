package net.danlew.predictit.api.model

import com.squareup.moshi.JsonClass
import net.danlew.predictit.model.*
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
internal data class ApiMarket(
  val id: Long,
  val name: String,
  val url: String,
  val timeStamp: ZonedDateTime,
  val status: MarketStatus,
  val contracts: List<ApiContract>
) {

  fun toMarketWithPrices() = MarketWithPrices(
    market = toMarket(),
    lastTradePrices = toContractPrices()
  )

  private fun toMarket() = Market(
    id = MarketId(id),
    name = name,
    url = url,
    status = status,
    contracts = contracts.map(ApiContract::toContract)
  )

  private fun toContractPrices() = contracts.associate {
    ContractId(it.id) to ContractPrice(
      timeStamp = timeStamp,
      price = it.lastTradePrice
    )
  }

}
