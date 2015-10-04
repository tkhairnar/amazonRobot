package com.workoutcash.amrobot.security

import scala.Option


/**
 * Created by tushar on 4/10/15.
 */
object SecurityRepo {


  /*
    First check for username and password matched
    Find token from token-map return if token is not generated create one token
    and return one.

    TODO : Expire the token
 */
  def createToken(user: String, password: String): Option[WorkoutCashSecurityToken] = {
    if(authenticate(user,password)) {
      tokenToUserMap.find( _._2.equals(user)) match {
        case Some(t) => Some(t._1)
        case None =>
          val token =  generateToken(user)
          validTokens.add(token)
          Some(token)
      }
    }
    else None
  }

  def generateToken(user: String): WorkoutCashSecurityToken = {
    val userOpt = users.find(_.userId.equals(user))
    val token = WorkoutCashSecurityToken(java.util.UUID.randomUUID().toString, System.currentTimeMillis(), userOpt.get)
    tokenToUserMap.put(token,userOpt.get)
    token
  }

  def validateToken(token:String) : (Boolean, Option[User]) = {
    validTokens.find( _.string.equals(token)) match {
      case Some(t) => (true, Some(t.user)) //TODO check for expire
      case None => (false,None)
    }
  }

  //serId: String, email: String, pass: String, address: String
  val users = Seq[User](
    User("tushar", "tushar@workout.cash","bavdhan","bavdhan"),
    User("vishwesh", "vishwesh@workout.cash","varje","varje"),
    User("sutirth", "sutirth@workout.cash","lohgaon","lohgaon"),
    User("pankaj", "pankaj@workout.cash","vimannager","vimannager"),
    User("rohan", "rohan@workout.cash","pune","pune"),
    User("pratik", "pratik@workout.cash","pune","pune"),
    User("michael", "michael@workout.cash","newzealand","newzealand"),
    User("nirmalya", "nirmalya@workout.cash","baner","baner")
  )

  val roleMap = Map[String,Seq[String]] (
    "backend-admin" -> Seq("vishwesh", "tushar", "nirmalya"),
    "backend-user" -> Seq("sutirth", "pankaj", "rohan", "pratik", "michael", "vishwesh", "tushar", "nirmalya")
  )

  val tokenToUserMap = scala.collection.mutable.Map[WorkoutCashSecurityToken,User]()

  val validTokens = scala.collection.mutable.HashSet[WorkoutCashSecurityToken]()


  def authenticate(user:String, pass:String) = {
    users.find(_.userId.equals(user)) match {
      case Some(u) => u.pass.equals(pass)
      case None => false
    }
  }

  def accessController(role: String, userName: String) = {
    val accessList : Option[Seq[String]] = roleMap.get(role)
    accessList  match {
      case Some(list) => list.contains(userName)
      case None => false
    }
  }

}
