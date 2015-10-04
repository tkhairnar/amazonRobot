
# AmazonRobot

AmazonRobot is simple app written to start/stop sbt applications deployed on Ec2

You can define apps in an config file or can add using REST API

Right now only SBT is supported but other app can also be supported - ant, maven, jar/java, stand-alone binaries

Supported actions are
 - start
 - stop
 - restart
 - rebuild (stop the app, compile it again and run.)
 - sync (do sync with git repository (credentials needs to be sent over wire) and then do a rebuild)

All app applications are protected. To call operations on App you need to first obtain token and pass it
in subsequent requests.

> `curl -X POST -d "user=tushar&password=23525" http://localhost:8080/token`

will give you this token

> `3a70f169-58a0-42f7-a6e0-0f9f4212d81e`

which you can use in sub-sequent requests

> `curl -H "Content-Type: application/json" -X POST -d '{"eid" : 1, "date":"5/7/1984", "stepCount" : 5, "calorie" : 100, "distance":5}' http://localhost:8080/health?access_token=3a70f169-58a0-42f7-a6e0-0f9f4212d81e`

Currently this application is written as POC for for three things listed below but eventually vision is to build robust application to control all amazon process directly through command line as well as mobile app
- Integrate spray with Swagger
- Integrate Authentication and Authorization using AccessToken
- Integrate SSL

## Steps to build and run prototype

To start the app simply run

> sbt run

To see swagger-ui go to http://localhost:8080

To run tests simply run

> sbt test

To pack this app as an installable run sbt pack. All dependency jars and start scripts are generate in `target/pack` folder

> sbt pack

## How to add Authentication and Authorization layer to your App

See EmployeeHealthService.scala

First you need to add WorkoutCashSecurityDirectives trait

> `trait EmployeeHealthService  extends HttpService with WorkoutCashSecurityDirectives {`

While defining your route use directives as follows - authenticateToken and authorize(authorization("backend-admin", authInfo.userId))

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
           }`


### Notes about current implementation

 - User has to obtain access token using his userName and password
 - Use this token to access secured REST end-points
 - Currently security is based on access_token explicit query parameter but we might move the token to header section.
 - Current implementation *does support* userName/Password based authentication. Use `authenticateUserPass` directive for password based authentication.
 - This is only unit-tested and some manual testing using cURL. If you find any issues please report to me ASAP
 - TODO : check for token expiry
 - TODO : Abstract out hard-coded maps here to an interface with default neo4j or some other implementation. Currently everything is in-memory. See class SecurityRepo

## Documenting your REST API using Swagger

See EmployeeHealthService.scala

Annotate your HttpService as follows

    @Api(value = "/health", description = "Operation about Employee Health Data", produces = "application/json", position = 1)
    trait EmployeeHealthService  extends HttpService with WorkoutCashSecurityDirectives {

Then annotate individual Routes as follows

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