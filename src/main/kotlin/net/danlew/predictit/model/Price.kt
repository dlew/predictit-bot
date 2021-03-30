package net.danlew.predictit.model

/**
 * The price of a [Contract].
 *
 * Stored as an [Int] between 0-100 (easier to handle than BigDecimal).
 */
inline class Price(val value: Int) {
  init {
    require(value in 0..100)
  }
}