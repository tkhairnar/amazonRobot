package com.workoutcash.amrobot.app

import com.typesafe.config.ConfigFactory

object AmazonRobotConfig {
  private val config = ConfigFactory.load()

  object HttpConfig {
    private val httpConfig = config.getConfig("http")
    lazy val interface = httpConfig.getString("interface")
    lazy val port = httpConfig.getInt("port")
  }
  //Config Settings
}
