package net.danlew.predictit.api.model

import com.squareup.moshi.JsonClass
import net.danlew.predictit.model.Contract
import net.danlew.predictit.model.ContractId
import net.danlew.predictit.model.MarketStatus
import net.danlew.predictit.model.Price
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
internal data class ApiContract(
  val id: Long,
  val name: String,
  val shortName: String,
  val image: String,
  val status: MarketStatus,
  val lastTradePrice: Double
) {

  fun toContract() = Contract(
    id = ContractId(id),
    name = name,
    shortName = shortName,
    image = image,
    status = status
  )

}