package com.workoutcash.amrobot.service

import com.wordnik.swagger.annotations._
import com.workoutcash.amrobot.Json4sSupport

//import spray.httpx.Json4sSupport
import com.workoutcash.amrobot.security.WorkoutCashSecurityDirectives
import spray.routing.HttpService


@Api(value = "/health", description = "Operation about Employee Health Data", produces = "application/json", position = 1)
trait EmployeeHealthService  extends HttpService with WorkoutCashSecurityDirectives {

  import Json4sSupport._

  val routes = getHealthData ~ postHealthData

  @ApiOperation(value = "Post employee health data", notes = "", nickname = "postData", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "EmployeeHealthData", dataType = "EmployeeHealthData", required = true, paramType = "body"),
    new ApiImplicitParam(name = "access_token", value = "Access Token", dataType = "String", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Employee HealthData Received"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def postHealthData =
    path("health") {
      post {
        authenticateToken { authInfo =>
          authorize(authorization("backend-admin", authInfo.userId)) {
            entity(as[EmployeeHealthData]) { healthData =>
              println("Received health data " + healthData)
              complete("Received health data")
            }
          }
        }
      }
    }


  @ApiOperation(value = "Get Employee Health Data", notes = "Returns health data", httpMethod = "GET", response = classOf[EmployeeHealthData])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "employeeId", value = "ID of employee", required = true, dataType = "integer", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Employee HealthData"),
    new ApiResponse(code = 404, message = "Employee not found"),
    new ApiResponse(code = 400, message = "Invalid ID supplied")
  ))
  def getHealthData = get {
    path("health" / IntNumber) { id =>
      complete(Seq(
        EmployeeHealthData(id, "" + new java.util.Date(), 10, 100, 5),
        EmployeeHealthData(id+1, "" + new java.util.Date(), 10, 100, 5)
      ))
    }
  }
}

case class EmployeeHealthData(eid: Int, date: String, stepCount: Int, calorie: Int, distance: Int)
