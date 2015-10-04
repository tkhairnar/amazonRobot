package com.workoutcash.amrobot.core

import akka.actor.Actor



/**
 * Created by tushar on 3/10/15.
 */

/*
  Create a App specific actor and delegate to it
 */
class RobotActor extends Actor {
  def receive: Receive = {

    case StartApp(id) => {
      print("Starting App with id="+id)
    }

  }
}
