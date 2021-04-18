package net.danlew.predictit.test

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import net.danlew.predictit.db.Database
import net.danlew.predictit.db.SqlDelightDatabase
import net.danlew.predictit.db.sql.SqlDatabase
import net.danlew.predictit.util.Constants
import java.time.Clock

fun createMemoryDb(): Database {
  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  driver.execute(null, "PRAGMA foreign_keys=ON", 0)
  SqlDatabase.Schema.create(driver)

  return SqlDelightDatabase.createDb(driver, Constants.EXPIRATION_DURATION, Clock.systemUTC())
}