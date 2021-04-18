package net.danlew.predictit.analyzer

import net.danlew.predictit.model.*
import net.danlew.predictit.test.basicContractPrices
import net.danlew.predictit.test.basicMarket
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class ContractAddedAnalyzerTest {

  @Test
  fun analyze() {
    val closedMarket = basicMarket().copy(status = MarketStatus.CLOSED)
    val openMarket = basicMarket().copy(status = MarketStatus.OPEN)
    val openMarketWithPrices = MarketWithPrices(openMarket, basicContractPrices())

    val newContract = Contract(
      id = ContractId(2L),
      name = "Homer Simpson",
      shortName = "Homer",
      image = "https://path.to/image/homer",
      status = MarketStatus.OPEN,
    )
    val marketWithNewContracts = openMarket.copy(
      status = MarketStatus.OPEN,
      contracts = openMarket.contracts + newContract
    )
    val marketWithNewContractsWithPrices = MarketWithPrices(
      market = marketWithNewContracts,
      pricesAtTime = PricesAtTime(
        timeStamp = Instant.ofEpochMilli(2000),
        prices = openMarketWithPrices.pricesAtTime.prices + mapOf(newContract.id to Price(25))
      )
    )

    val expectedNotification = Notification(
      marketId = marketWithNewContracts.id,
      contractId = newContract.id,
      type = Notification.Type.CONTRACT_ADDED
    )

    // A market with a new contract alerts!
    assertEquals(
      setOf(expectedNotification),
      ContractAddedAnalyzer.analyze(openMarket, marketWithNewContractsWithPrices)
    )

    // A market with no new contracts should yield nothing
    assertEquals(
      emptySet<Notification>(),
      ContractAddedAnalyzer.analyze(openMarket, openMarketWithPrices)
    )

    // Closed or previously unseen markets cannot indicate new contracts
    assertEquals(
      emptySet<Notification>(),
      ContractAddedAnalyzer.analyze(null, marketWithNewContractsWithPrices)
    )

    assertEquals(
      emptySet<Notification>(),
      ContractAddedAnalyzer.analyze(closedMarket, marketWithNewContractsWithPrices)
    )
  }

}