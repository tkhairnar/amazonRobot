package com.workoutcash.amrobot

import spray.routing.RequestContext
import spray.routing.Rejection
import scala.concurrent.Future

/**
 * Created by tushar on 3/10/15.
 */
package object security {

  type ParamExtractor = RequestContext => Map[String, String]

  case class User(userId: String, email: String, pass: String, address: String)

  case class Consumer(appKey: String, appSecret: String, description: String)

  case class AccessToken(accessToken: String, userId: String)

}
