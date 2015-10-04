package com.workoutcash.amrobot.core

import akka.actor.Actor

/**
 * Created by tushar on 3/10/15.
 */


case class StartApp(name:String)
case class StopApp(name:String)
case class ReStartApp(name:String)
case class RebuildApp(name:String)
case class SyncApp(name:String)
case class GetAppInfo(name:String)

class AppActor(app:RobotApp) extends Actor {

  def receive = {
    case _ =>
      println("Received request ")
  }
}
