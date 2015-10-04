package com.workoutcash.amrobot.security

import com.wordnik.swagger.annotations._
import com.workoutcash.amrobot.security.authenticators.{AccessTokenHandler, UserPassHandler}
import spray.http.{HttpResponse, StatusCodes}
import spray.routing.{HttpService, Route}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by tushar on 3/10/15.
 *
 * Currently security is based on access_token explicit query parameter but we might move the token to header
 * section. Current implementation does support userName/Password authentication. Use authenticateUserPass directive
 * for password based authentication.
 *
 * This is only unit-tested and some manual testing using cURL. If you find any issues please report to me ASAP
 *
 * TODO : Add case class Token insted of String token with expiry attribute
 * TODO : check for token expiry
 * TODO : Abstract out hard-coded maps here to an interface with default neo4j or some other implementation
 *
 */
object WorkoutCashHttpAuth {

  val authenticator = (params: Map[String, String]) => Future  {
    val mayBeToken = params.get("access_token")
    mayBeToken flatMap { token =>
      //println("Token I got in my authenticator " + token)
      val (validToken, user) = SecurityRepo.validateToken(token)
      if(validToken)
        user
      else None
    }
  }

  def accessController(role: String, userName: String) : Boolean = SecurityRepo.accessController(role,userName)

  def createToken(user: String, password: String) : Option[WorkoutCashSecurityToken] = {
    SecurityRepo.createToken(user,password)
  }
}

case class WorkoutCashSecurityToken(string:String, birthTime:Long, user: User)

trait WorkoutCashSecurityDirectives {
  val authenticateToken = AccessTokenHandler.AccessTokenAuthenticator(authenticator = WorkoutCashHttpAuth.authenticator).apply()

  val authenticateUserPass = UserPassHandler.UserPassAuthenticator(authenticator = WorkoutCashHttpAuth.authenticator).apply()

  def authorization(role:String, userName: String) = WorkoutCashHttpAuth.accessController(role, userName)
}

@Api(value = "/token", description = "WorkoutCash's Access Token EndPoint", produces = "application/json", position = 0)
trait WorkoutCashSecurity extends HttpService {

  val routes = tokenMgmtRoute

  @ApiOperation(value = "WorkoutCash's Access Token EndPoint", notes = "", nickname = "postData", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "user", value = "Workoutcash UserId", dataType = "string", required = true, paramType = "body"),
    new ApiImplicitParam(name = "password", value = "Workoutcash Password", dataType = "String", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "You AccessToken Granted"),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 401, message = "Authentication has failed")
  ))
  def tokenMgmtRoute = path("token") {
    post {
      formFields('user, 'password) { (user, password) =>
        println("Creating valid token for user="+ user)
        WorkoutCashHttpAuth.createToken(user,password) match {
          case Some(n:WorkoutCashSecurityToken) => complete(n.string)
          case None => complete(HttpResponse(StatusCodes.Unauthorized, "Wrong username/password"))
        }
      }
    }
  }
}