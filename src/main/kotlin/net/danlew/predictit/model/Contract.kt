package net.danlew.predictit.model

/**
 * An individual contract within an [Market] (otherwise known as brackets).
 */
data class Contract(
  val id: ContractId,
  val name: String,
  val image: String
)