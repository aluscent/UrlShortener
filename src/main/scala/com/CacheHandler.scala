package com

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import scredis.Client

import java.util.concurrent.Executors
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.language.postfixOps

class CacheHandler {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))
  implicit val redisClient: Client = Client(host = "localhost", port = 6379)

  redisClient.auth("eYVX7EwV")

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
}
