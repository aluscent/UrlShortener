package com

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import scredis.Client

import java.util.concurrent.Executors
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.language.postfixOps

class CacheHandler(host: String, port: Int)
                  (implicit system: ActorSystem, executionContext: ExecutionContext) {
  val client: Client = Client(host, port)

  client.auth("eYVX7EwV")

  def lookupLongUrl(url: String): Option[Uri] =
    Await.result(client.auth("eYVX7EwV").flatMap(_ => client.get(url)), 1 seconds).map(Uri(_))

  def registerNewUrl(longUrl: String, shortUrl: String): (Boolean, String) =
    (Await.result(client
      .auth("eYVX7EwV")
      .flatMap(_ => client.set(shortUrl, longUrl)),
      3 seconds
    ), shortUrl)


}
