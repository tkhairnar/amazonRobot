package com.workoutcash.amrobot

import java.util.concurrent.TimeUnit.SECONDS

import akka.util.Timeout
import com.workoutcash.amrobot.security.WorkoutCashHttpAuth
import com.workoutcash.amrobot.service.{EmployeeHealthData, EmployeeHealthService}
import org.scalatest.{FlatSpec, Matchers}
import spray.routing.AuthenticationFailedRejection
import spray.routing.AuthenticationFailedRejection.CredentialsRejected
import spray.testkit.ScalatestRouteTest

import scala.concurrent.duration.FiniteDuration

/**
 * Created by tushar on 4/10/15.
 */
class EmployeeHealthServiceTest extends FlatSpec with ScalatestRouteTest with Matchers with EmployeeHealthService {

  import Json4sSupport._

  val actorRefFactory = system
  implicit val timeout = Timeout(new FiniteDuration(15, SECONDS))
  implicit val routeTestTimeout = RouteTestTimeout(timeout.duration)

  //####################################################################
  //TEST CASE FOR UNPROTECTED RESOURCES
  //####################################################################

  "HealthService" should
    "respond to unprotected resource GET HEALTH" in {
    Get(s"/health/1234") ~> routes ~> check {
      val seq = responseAs[Seq[EmployeeHealthData]]
      seq.length should be (2)
      seq(0).eid should be (1234)
      seq(1).eid should be (1235)
    }
  }

  "HealthService" should
    "respond to accessToken protected POST HEALTH if valid access token is provided" in {
    val validAccessToken = WorkoutCashHttpAuth.createToken("tushar","bavdhan").get
    val tokenString = validAccessToken.string
    println("Got ValidToken = " + tokenString)
    Post(s"/health?access_token=$tokenString", EmployeeHealthData(1234, "" + new java.util.Date(), 10, 100, 5)) ~> routes ~> check {
      responseAs[String] should include("Received health data")
    }
  }

  "HealthService" should
    "not respond to accessToken protected POST HEALTH if access token is not valid" in {
    val invalidAccessToken = "sfd3454543ergr"
    Post(s"/health?access_token=$invalidAccessToken", EmployeeHealthData(1234, "" + new java.util.Date(), 10, 100, 5)) ~> routes ~> check {
      rejection === List(AuthenticationFailedRejection(CredentialsRejected,List()))
      //status === Forbidden
    }
  }

  "HealthService" should
    "not respond to accessToken protected POST HEALTH if access token is missing" in {
    Post(s"/health", EmployeeHealthData(1234, "" + new java.util.Date(), 10, 100, 5)) ~> routes ~> check {
      rejection === List(AuthenticationFailedRejection(CredentialsRejected,List()))
    }
  }
}
