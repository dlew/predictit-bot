package net.danlew.predictit.util

import java.time.Duration

object Constants {

  const val DATA_DIR = "data"
  const val DB_NAME = "database.sqlite"

  val EXPIRATION_DURATION: Duration = Duration.ofDays(1)

}