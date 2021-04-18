package net.danlew.predictit.api.model

import com.squareup.moshi.JsonClass
import net.danlew.predictit.model.*
import java.time.Instant
import kotlin.math.roundToInt

@JsonClass(generateAdapter = true)
internal data class ApiMarket(
  val id: Long,
  val name: String,
  val shortName: String,
  val image: String,
  val url: String,
  val timeStamp: Instant,
  val status: MarketStatus,
  val contracts: List<ApiContract>
) {

  fun toMarketWithPrices() = MarketWithPrices(
    market = toMarket(),
    pricesAtTime = PricesAtTime(timeStamp = timeStamp, prices = toContractPrices())
  )

  private fun toMarket() = Market(
    id = MarketId(id),
    name = name,
    shortName = shortName,
    image = image,
    url = url,
    status = status,
    contracts = contracts.map(ApiContract::toContract)
  )

  private fun toContractPrices() = contracts.associate {
    ContractId(it.id) to Price((it.lastTradePrice * 100).roundToInt())
  }

}
