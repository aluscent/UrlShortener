package com

import com.CacheHandler
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Route

import java.util.concurrent.Executors
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}
import akka.http.scaladsl.server.Directives._

object AllService {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))

  implicit class UrlTools(url: String) {
    def buildShortUrl: String =
      url.replaceAll("(a|e|o|i|u|y|A|E|O|I|U|Y)", "")
  }

  def main(args: Array[String]): Unit = {
    implicit val redisClient: CacheHandler = new CacheHandler(host = "localhost", port = 6379)

    val chainedRoute: Route =
      path(Segment) { shortUrl =>
        println(s"[GET] URL: $shortUrl")
        redisClient.lookupLongUrl(shortUrl) match {
          case Some(value) => redirect(value.withScheme("https"), StatusCodes.Found)
          case None => complete(404, "The URL is invalid.")
        }
      } ~ path("app" / "api" / "create") {
        (post & extractLog & pathEndOrSingleSlash & extractRequest) { (_, payload) =>
          println(s"[POST] Triggered. Payload: $payload")

          val strict = payload.entity
            .toStrict(1 seconds)
            .map(item => item.data.utf8String)

          strict.value map {
            case Success(value) =>
              val (registered, newUrl) = redisClient.registerNewUrl(value, value.buildShortUrl)
              if (registered) {
                complete(200, s"New URL registered: $newUrl")
              } else {
                complete(501, "Couldn't register URL.")
              }
            case Failure(exception) =>
              complete(502, s"This URL couldn't register.\nReason: ${exception.getCause}")
          } getOrElse complete(503, "Payload timed out.")
        }
      }

    Http().newServerAt("localhost", 8081).bind(chainedRoute)
  }
}
