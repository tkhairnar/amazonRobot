package com.workoutcash.amrobot.app

import akka.actor._
import com.gettyimages.spray.swagger._
import com.wordnik.swagger.model.ApiInfo
import com.workoutcash.amrobot.security.WorkoutCashSecurity
import com.workoutcash.amrobot.service.EmployeeHealthService
import spray.routing._

import scala.reflect.runtime.universe._

class AmazonRobotActor
  extends HttpServiceActor
  with ActorLogging {

  override def actorRefFactory = context

  val accessToken = new WorkoutCashSecurity {
    def actorRefFactory = context
  }

  val employeeHealthData = new EmployeeHealthService {
    def actorRefFactory = context
  }

  def receive = runRoute( accessToken.routes ~ employeeHealthData.routes ~ swaggerService.routes ~
    get {
      pathPrefix("") {
        pathEndOrSingleSlash {
          getFromResource("swagger-ui/index.html")
        }
      } ~
        getFromResourceDirectory("swagger-ui")
    })

  val swaggerService = new SwaggerHttpService {
    //override def apiTypes = Seq(typeOf[PetHttpService], typeOf[UserHttpService], typeOf[EmployeeHealthService])
    override def apiTypes = Seq(typeOf[EmployeeHealthService])

    override def apiVersion = "2.0"

    override def baseUrl = "/"

    // let swagger-ui determine the host and port
    override def docsPath = "api-docs"

    override def actorRefFactory = context

    override def apiInfo = Some(new ApiInfo("Workout.cash Employee Health API", "Employee HealthData Service", "TOC Url", "Tushar(tushar@workout.cash)", "Apache V2", "http://www.apache.org/licenses/LICENSE-2.0"))

    //authorizations, not used
  }
}
