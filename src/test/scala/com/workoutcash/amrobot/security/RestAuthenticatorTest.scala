package com.workoutcash.amrobot.security

import akka.util.Timeout
import com.workoutcash.amrobot.security.authenticators.{UserPassHandler, AccessTokenHandler}
import com.workoutcash.amrobot.security.TestAuthHandler.{TestAccessTokenHandler, TestUserPassHandler}
import org.scalatest.{FlatSpec, Matchers}
import spray.http.HttpResponse
import spray.routing.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import spray.routing.{AuthenticationFailedRejection, Route, HttpService}
import spray.testkit.ScalatestRouteTest
import spray.http.StatusCodes.OK
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit.SECONDS

/**
 * Created by tushar on 4/10/15.
 */
class RestAuthenticatorTest extends FlatSpec with ScalatestRouteTest with Matchers with HttpService {

  val actorRefFactory = system
  implicit val timeout = Timeout(new FiniteDuration(15, SECONDS))
  implicit val routeTestTimeout = RouteTestTimeout(timeout.duration)

  val accessTokenHandlerForTest = AccessTokenHandler.AccessTokenAuthenticator(authenticator = TestAccessTokenHandler.authenticator).apply()
  val userPassHandlerForTest = UserPassHandler.UserPassAuthenticator(authenticator = TestUserPassHandler.authenticator).apply()

  val openRoutes = {
    get {
      pathSingleSlash
      path("ping") {
        complete(HttpResponse(OK, "WELCOME"))
      }
    }
  }

  val tokenProtectedRoutes: Route = {
    pathPrefix("user") {
      accessTokenHandlerForTest { user =>

        /*
         *    Following are protected resources.
         *   Requires valid access_token of p3 user.
         */

        get {
          path("items") {
            complete(HttpResponse(OK, "Display user's item"))
          }
        }

      }
    }
  }

  /**
   * Following route shows how authenticators can be composed. Here userPassHandlerForTest &
   * appCredentialHandlerForTest has been composed to validate app and user's credentials
   */

  val userPassProtectedRoutes = {
    pathPrefix("authenticate") {
      userPassHandlerForTest { user =>
          /*
            *   Following are protected resources.
            *   Request would gone through userPass to unlock the resources.
            */
          get {
            pathSingleSlash {
              complete(HttpResponse(OK, "Welcome User !!!"))
            }
          }
        }
      }
    }

  val testRoutes = openRoutes ~ pathPrefix("secure")(tokenProtectedRoutes ~ userPassProtectedRoutes )

  //####################################################################
  //TEST CASE FOR UNPROTECTED RESOURCES
  //####################################################################

  "RestAuthenticator" should
    "respond to unprotected resource" in {
    Get("/ping") ~> testRoutes ~> check {
      responseAs[String] should include("WELCOME")
    }
  }

  //####################################################################
  //TEST CASES FOR ACCESS TOKEN PROTECTED RESOURCES
  //####################################################################

  "RestAuthenticator" should
    "respond to accessToken protected resources if valid access token is provided" in {
    val validAccessTokenString = "df4545665drgdfg"
    Get(s"/secure/user/items?access_token=$validAccessTokenString") ~> testRoutes ~> check {
      responseAs[String] should include("Display user's item")
    }
  }

  "RestAuthenticator" should
    "not respond to accessToken protected resource if access token is not valid" in {
    val invalidAccessToken = "sfd3454543ergr"
    Get(s"/secure/user/items?access_token=$invalidAccessToken") ~> testRoutes ~> check {
      rejection === AuthenticationFailedRejection(CredentialsRejected, List())
    }
  }

  "RestAuthenticator" should
    "not respond to accessToken protected resource if access token is missing" in {
    Get(s"/secure/user/items") ~> testRoutes ~> check {
      rejection === AuthenticationFailedRejection(CredentialsMissing, List())
    }
  }



}
