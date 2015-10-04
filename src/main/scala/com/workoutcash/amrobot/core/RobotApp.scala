package com.workoutcash.amrobot.core

/**
 * Created by tushar on 3/10/15.
 */
case class RobotApp(name : String, path : String, mainClass : String, buildType : BuildType.BuildType, scm : SCMType.SCMType) {
  /* Lets add this info later
  var startTime : Long = -1
  var stopTime : Long = -1
  */
}

object BuildType extends Enumeration {
  type BuildType = Value
  val SBT, SBP_PACK, MAVEN, ANT, SCRIPT = Value
}

object SCMType extends Enumeration {
  type SCMType = Value
  val GIT, SVN = Value
}

object AppState extends Enumeration {
  type AppState = Value
  val RUNNING, UNKNOWN, STOPPED, IN_SYNC, REBUILDING, RESTARTING = Value
}

case class AppOperationResult(pid:Int, state : AppState.AppState)

trait RobotAppOperations {

  def start(app:RobotApp) : AppOperationResult

  def restart(app:RobotApp, prevState : AppOperationResult) : AppOperationResult = {
    stop(app, prevState)
    start(app)
  }

  def rebuild(app:RobotApp, prevState : AppOperationResult) : AppOperationResult

  def stop(app:RobotApp, prevState : AppOperationResult) : AppOperationResult

  def sync(app:RobotApp, prevState : AppOperationResult) : AppOperationResult

}

trait SBTPackRobotOperations extends RobotAppOperations {

  def start(app:RobotApp) : AppOperationResult = {
    AppOperationResult(0,AppState.UNKNOWN)
  }

  def rebuild(app:RobotApp, prevState : AppOperationResult): AppOperationResult = {
    AppOperationResult(0,AppState.UNKNOWN)
    restart(app, prevState)
  }
}

trait GitRobotOperations extends RobotAppOperations {
  def sync(app:RobotApp, prevState : AppOperationResult) : AppOperationResult = {
    //call git pull, get password from ThreadLocal
    rebuild(app, prevState)
  }
}