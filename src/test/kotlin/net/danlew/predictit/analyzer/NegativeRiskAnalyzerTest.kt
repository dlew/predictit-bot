package net.danlew.predictit.analyzer

import net.danlew.predictit.model.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class NegativeRiskAnalyzerTest {

  @Test
  fun analyze() {
    val expectedNotification = Notification(
      marketId = MarketId(1L),
      contractId = null,
      type = Notification.Type.NEGATIVE_RISK
    )

    // Close, but no cigar
    assertEquals(
      emptySet<Notification>(),
      NegativeRiskAnalyzer.analyze(null, generateMarket(Price(20), Price(10), Price(20)))
    )

    assertEquals(
      emptySet<Notification>(),
      NegativeRiskAnalyzer.analyze(null, generateMarket(Price(50), Price(10), Price(40)))
    )

    assertEquals(
      emptySet<Notification>(),
      NegativeRiskAnalyzer.analyze(null, generateMarket(Price(90), Price(10), Price(10)))
    )

    // Yay negative risk
    assertEquals(
      setOf(expectedNotification),
      NegativeRiskAnalyzer.analyze(null, generateMarket(Price(90), Price(10), Price(11)))
    )

    assertEquals(
      setOf(expectedNotification),
      NegativeRiskAnalyzer.analyze(null, generateMarket(Price(75), Price(75), Price(75)))
    )
  }

  @Test
  fun excludesOneCentContracts() {
    // One cent contracts are basically impossible to buy, so you can't factor them into negative risk
    assertEquals(
      emptySet<Notification>(),
      NegativeRiskAnalyzer.analyze(null, generateMarket(Price(90), Price(20), Price(1)))
    )
  }

  @Test
  fun doesNotAnalyzeClosedMarkets() {
    val base = generateMarket(Price(75), Price(75), Price(75))
    val closedMarket = base.copy(market = base.market.copy(status = MarketStatus.CLOSED))
    assertEquals(
      emptySet<Notification>(),
      NegativeRiskAnalyzer.analyze(null, closedMarket)
    )
  }

  private fun generateMarket(price1: Price, price2: Price, price3: Price) = MarketWithPrices(
    market = Market(
      id = MarketId(1L),
      name = "Skub?",
      shortName = "Skub?",
      image = "",
      url = "",
      status = MarketStatus.OPEN,
      contracts = listOf(
        Contract(
          id = ContractId(1L),
          name = "Pro Skub",
          shortName = "Pro Skub",
          image = "",
          status = MarketStatus.OPEN
        ),
        Contract(
          id = ContractId(2L),
          name = "Anti Skub",
          shortName = "Anti Skub",
          image = "",
          status = MarketStatus.OPEN
        ),
        Contract(
          id = ContractId(3L),
          name = "Skub",
          shortName = "Skub",
          image = "",
          status = MarketStatus.OPEN
        )
      )
    ),
    pricesAtTime = PricesAtTime(
      timeStamp = Instant.ofEpochSecond(1),
      prices = mapOf(
        ContractId(1L) to price1,
        ContractId(2L) to price2,
        ContractId(3L) to price3
      )
    )
  )
}