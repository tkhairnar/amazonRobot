package com.workoutcash.amrobot.app

import akka.actor.ActorDSL._
import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.io.IO
import akka.io.Tcp._
import spray.can.Http

object AmazonRobot extends App {
  implicit val system = ActorSystem("WorkOutCashRESTService")

  /* Spray Service */
  val service= system.actorOf(Props[AmazonRobotActor], "WorkoutCashRESTSERVICE")

  val ioListener = actor("ioListener")(new Act with ActorLogging {
    become {
      case b @ Bound(connection) => log.info(b.toString)
    }
  })

  IO(Http).tell(Http.Bind(service, AmazonRobotConfig.HttpConfig.interface, AmazonRobotConfig.HttpConfig.port), ioListener)

}
