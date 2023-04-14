package com

import com.mongodb.{ServerApi, ServerApiVersion}
import org.mongodb.scala.bson.Document
import org.mongodb.scala.{ConnectionString, MongoClient, MongoClientSettings}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.language.postfixOps
import scala.util.{Try, Using}

class DatabaseHandler(host: String, port: Int)(implicit executionContext: ExecutionContextExecutor) {
  val serverApi: ServerApi = ServerApi.builder.version(ServerApiVersion.V1).build()
  val settings: MongoClientSettings = MongoClientSettings
    .builder()
    .applyConnectionString(ConnectionString(s"mongodb://root:example@$host:$port"))
    .serverApi(serverApi)
    .build()

  def testConnection: Try[Unit] = Using(MongoClient(settings)) { client =>
    // Send a ping to confirm a successful connection
    val database = client.getDatabase("admin")
    val ping = database.runCommand(Document("ping" -> 1)).head()

    Await.result(ping, 10 seconds)
    System.out.println("Pinged your deployment. You successfully connected to MongoDB!")
  }

  def lookupDatabaseClickRates(url: String): Try[String] = Using(MongoClient(settings)) { client =>
    val collection = client
      .getDatabase("snapptrip")
      .getCollection("clickratescollection")
    val fetchedUrl = collection.find().head().map(_.toString())

    Await.result(fetchedUrl, 10 seconds)
  }

//  def insertIntoDatabaseClickRate(url: String):

  def lookupDatabaseUrls(url: String): Try[String] = Using(MongoClient(settings)) { client =>
    val collection = client
      .getDatabase("snapptrip")
      .getCollection("urlscollection")
    val fetchedUrl = collection.find().head().map(_.toString())

    Await.result(fetchedUrl, 10 seconds)
  }

//  def insertIntoDatabaseUrl(url: String, longUrl: String): Boolean =
//    querySubmitter
//      .prepareStatement(s"insert into urls (SHORTURL, LONGURL) values ('$url', '$longUrl')")
//      .execute()
}
