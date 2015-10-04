package com.workoutcash.amrobot.service

import com.wordnik.swagger.annotations._
import spray.http.StatusCodes.OK
import spray.routing.HttpService

@Api(value = "/e/onboard", description = "Operations for on-boarding new employees", produces = "application/json", position = 3)
trait EmployeeOnBoardingService extends HttpService {

  val routes = postOnBoardRequest

  @ApiOperation(value = "Post employee onboarding information", notes = "", nickname = "postData", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "EmployeeOnBoardRequest", dataType = "EmployeeHealthData", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Employee HealthData Received"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def postOnBoardRequest =
    path("/e/onboard") {
      post {
        complete(OK)
      }
    }

}

case class EmployeeOnBoardRequest(name : String, organizationId : Int, emailId : String)
