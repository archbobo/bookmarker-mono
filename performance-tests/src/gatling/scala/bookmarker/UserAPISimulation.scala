package bookmarker

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class UserAPISimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val userIdFeeder = csv("userIds.csv").random
  val newUsersFeeder = csv("newUsers.csv").random
  val credentialsFeeder = csv("credentials.csv").random

  var randomString = Iterator.continually(Map("randstring" -> ( Random.alphanumeric.take(20).mkString )))
  var randomInt = Iterator.continually(Map("randinteger" -> ( Random.nextInt(1000))))

  val getUserById = feed(userIdFeeder)
               .exec(http("UserById").get("/api/users/${userId}"))
               .pause(1)

  var token = ""
  val login = feed(credentialsFeeder)
                .exec(http("Login")
                .post("/api/auth/login")
                .body(StringBody(
                  """
                    {
                      "username":"${username}",
                      "password":"${password}"
                    }
                  """)).asJSON
                .check(status.is(200),jsonPath("$..access_token").saveAs("token"))
              )
              .exec(session => {
                  token = session("token").as[String].trim
                  session
                }
              )
              .pause(1)

  val createUser = feed(randomString)
                    .exec(http("Create User")
                        .post("/api/users")
                        .body(StringBody(
                          """
                            { "name":"${randstring}",
                            "email":"${randstring}@gmail.com",
                            "password":"${randstring}"
                            }
                          """)).asJSON
                    )
                    .pause(1)

  val deleteUserById = feed(randomInt)
    .exec(http("DeleteUser").delete("/api/users/${randinteger}"))
    .pause(1)

  // Now, we can write the scenario as a composition
  val scnGetUserById = scenario("Get User Profile By Id").exec(getUserById).pause(2)
  val scnLogin = scenario("Login").exec(login).pause(2)
  val scnCreateUser = scenario("Create New User").exec(createUser).pause(2)
  //val scnDeleteUser = scenario("Delete User By Id").exec(deleteUserById).pause(2)

  //setUp(scn.inject(atOnceUsers(10)).protocols(httpConf))

  setUp(
      scnGetUserById.inject(rampUsers(2) over (10 seconds)),
      scnLogin.inject(rampUsers(2) over (10 seconds)),
      scnCreateUser.inject(rampUsers(2) over (10 seconds))
      //scnDeleteUser.inject(rampUsers(5) over (10 seconds))
  ).protocols(httpConf)

}

