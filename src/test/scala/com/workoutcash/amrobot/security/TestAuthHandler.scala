package com.workoutcash.amrobot.security

import com.workoutcash.amrobot.security.{AccessToken, User}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by tushar on 4/10/15.
 */
object TestAuthHandler extends TestData {

  object TestAccessTokenHandler {
    val authenticator = authFunction _
    private def authFunction(params: Map[String, String]) = Future {
      val mayBeToken = params.get("access_token")
      mayBeToken flatMap { token => if (token == validAccessTokenString) Some(validUser) else None }
    }
  }

  object TestUserPassHandler {
    val authenticator = authFunction _
    private def authFunction(params: Map[String, String]) = Future {
      val emailOpt = params.get("username")
      val passwordOpt = params.get("password")

      val mayBeUser = for {
        email <- emailOpt
        password <- passwordOpt
        user <- {
          if (email == validEmail && password == validPass)
            Some(validUser)
          else
            None
        }
      } yield user
      mayBeUser
    }
  }


}

trait TestData {
  // Following are the hard coded values
  val validAppKey = "fdg456575756dfd"
  val validAppSecret = "fgt0945454-45fdferer3"

  val validUserId = "u-100"
  val validEmail = "bob@gmail.com"
  val validPass = "pa55w0rd"

  val validUser = User(validUserId, validEmail, validPass, "Any Address....")

  val validAccessTokenString = "df4545665drgdfg"
  val validAccessToken = AccessToken(validAccessTokenString, validUserId)

}