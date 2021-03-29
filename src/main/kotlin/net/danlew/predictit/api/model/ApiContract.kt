package net.danlew.predictit.api.model

import com.squareup.moshi.JsonClass
import net.danlew.predictit.model.Contract
import net.danlew.predictit.model.ContractId
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
internal data class ApiContract(
  val id: Long,
  val name: String,
  val lastTradePrice: BigDecimal
) {

  fun toContract() = Contract(
    id = ContractId(id),
    name = name
  )

}