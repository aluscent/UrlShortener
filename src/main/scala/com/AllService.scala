package com

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Route
import scredis._

import java.util.concurrent.Executors
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}
import akka.http.scaladsl.server.Directives._

object AllService {
  private def buildShortUrl(url: String): String =
    url.replaceAll("(a|e|o|i|u|y|A|E|O|I|U|Y)", "")

  private def lookupLongUrl(url: String)(implicit client: Client): Option[Uri] =
    Await.result(client.auth("eYVX7EwV").flatMap(_ => client.get(url)), 1 seconds).map(Uri(_))

  private def registerNewUrl(longUrl: String)(implicit client: Client): (Boolean, String) =
    (Await.result(client
      .auth("eYVX7EwV")
      .flatMap(_ => client.set(buildShortUrl(longUrl), longUrl)),
      3 seconds
    ), buildShortUrl(longUrl))

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor =
  ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))


  def main(args: Array[String]): Unit = {

    implicit val redisClient: Client = Client(host = "localhost", port = 6379)
    redisClient.auth("eYVX7EwV")

    val chainedRoute: Route =
      path("url" / Segment) { shortUrl =>
        println(s"[GET] URL: $shortUrl")
        lookupLongUrl(shortUrl) match {
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
              val (registered, newUrl) = registerNewUrl(value)
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
