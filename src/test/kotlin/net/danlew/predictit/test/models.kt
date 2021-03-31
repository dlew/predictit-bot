package net.danlew.predictit.test

import net.danlew.predictit.model.*
import java.time.ZonedDateTime

fun basicContractPrices() = mapOf(
  ContractId(1L) to PriceAtTime(ZonedDateTime.now(), Price(50))
)

fun basicMarket() = Market(
  id = MarketId(1L),
  name = "Who shot Burns?",
  image = "https://path.to/image/market",
  url = "https://predictit.org/example",
  status = MarketStatus.OPEN,
  contracts = listOf(basicContract())
)

fun basicContract() = Contract(
  id = ContractId(1L),
  name = "Maggie Simpson",
  image = "https://path.to/image/maggie"
)