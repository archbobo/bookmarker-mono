package bookmarker

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class BookmarksAPISimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val bookmarkUrlFeeder = csv("bookmarkUrls.csv").random
  val bookmarkIdsFeeder = csv("bookmarkIds.csv").random
  val userIdFeeder = csv("userIds.csv").random

  var randomString = Iterator.continually(Map("randstring" -> ( Random.alphanumeric.take(20).mkString )))
  var randomInt = Iterator.continually(Map("randinteger" -> ( Random.nextInt(5))))

  val getAllBookmarks = exec(http("AllBookmarks").get("/api/bookmarks")).pause(1)

  val getBookmarksByUser = feed(userIdFeeder)
                           .exec(http("BookmarksByUser")
                                .get("/api/bookmarks?userId=${userId}")
                           )
                           .pause(1)

  val getBookmarkById = feed(bookmarkIdsFeeder)
               .exec(http("BookmarkById").get("/api/bookmarks/${id}"))
               .pause(1)

  var token = ""

  val createBookmark = feed(bookmarkUrlFeeder)
                    //.exec(_.set("accessToken", accessToken))
                    .exec(http("Login")
                      .post("/api/auth/login")
                      .body(StringBody(
                        """
                          {
                            "username":"siva@gmail.com",
                            "password":"siva"
                          }
                        """)).asJSON
                      .check(status.is(200),jsonPath("$..access_token").saveAs("accessToken"))
                    )
                    .exec(http("Create Bookmark")
                        .post("/api/bookmarks")
                        .header("Authorization", "Bearer ${accessToken}")
                        .body(StringBody(
                          """
                            {
                              "url":"${url}"
                            }
                          """)).asJSON
                    )
                    .pause(1)

  // Now, we can write the scenario as a composition
  val scnGetAllBookmarks = scenario("Get All Bookmarks").exec(getAllBookmarks).pause(2)
  val scnGetBookmarksByUser = scenario("Get Bookmarks By User").exec(getBookmarksByUser).pause(2)
  val scnGetBookmarkById = scenario("Get Bookmark By Id").exec(getBookmarkById).pause(2)
  val scnCreateBookmark = scenario("Create New Bookmark").exec(createBookmark).pause(2)

  //setUp(scn.inject(atOnceUsers(10)).protocols(httpConf))

  setUp(
      scnGetAllBookmarks.inject(rampUsers(500) over (10 seconds)),
      scnGetBookmarksByUser.inject(rampUsers(200) over (10 seconds)),
      scnGetBookmarkById.inject(rampUsers(100) over (10 seconds)),
      scnCreateBookmark.inject(rampUsers(100) over (10 seconds))
  ).protocols(httpConf)

}
