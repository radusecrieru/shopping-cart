package com.shopping

import akka.actor.{Actor, Props}
import spray.http.MediaTypes._
import spray.routing._
import com.shopping.ProductCatalogItemJsonProtocol._
import spray.json._
import com.shopping.ProductCatalog._

object ProductCatalogServiceActor {
  def props: Props = Props[ProductCatalogServiceActor]
}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ProductCatalogServiceActor extends Actor with ProductCatalogService {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(productCatalogRoute)
}

// this trait defines our service behavior independently from the service actor
trait ProductCatalogService extends HttpService {

  val productCatalogRoute =
    path("product-catalog") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            productCatalog.toJson.compactPrint
          }
        }
      }
    }
}

