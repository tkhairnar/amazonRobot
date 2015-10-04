package com.workoutcash.amrobot.security

import java.util.concurrent.TimeUnit._

import akka.util.Timeout
import com.workoutcash.amrobot.Json4sSupport
import org.scalatest.{Matchers, FlatSpec}
import spray.http.{FormData, StatusCodes, HttpResponse}
import spray.testkit.ScalatestRouteTest
import spray.http.StatusCodes.OK

import scala.concurrent.duration.FiniteDuration

/**
 * Created by tushar on 4/10/15.
 */
class WorkoutCashSecurityTest extends FlatSpec with ScalatestRouteTest with Matchers with WorkoutCashSecurity {

  //import Json4sSupport._

  val actorRefFactory = system
  implicit val timeout = Timeout(new FiniteDuration(15, SECONDS))
  implicit val routeTestTimeout = RouteTestTimeout(timeout.duration)

  "WorkoutCashSecurity" should
    "return valid token when given right username and password" in {
    Post(s"/token", FormData(Map("user"->"tushar", "password"->"bavdhan"))) ~> tokenMgmtRoute ~> check {
      status === OK
    }
  }

  "WorkoutCashSecurity" should
    "return CredentialsRejected when given wrong password" in {
    Post(s"/token", FormData(Map("user"->"tushar", "password"->"bavdhan2"))) ~> tokenMgmtRoute ~> check {
      response === HttpResponse(StatusCodes.Unauthorized, "Wrong username/password")
    }
  }

  "WorkoutCashSecurity" should
    "return CredentialsRejected when given wrong username" in {
    Post(s"/token", FormData(Map("user"->"tushar2", "password"->"bavdhan"))) ~> tokenMgmtRoute ~> check {
      response === HttpResponse(StatusCodes.Unauthorized, "Wrong username/password")
    }
  }

}
