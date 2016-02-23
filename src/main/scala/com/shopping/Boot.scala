package com.shopping

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val productCatalog = system.actorOf(ProductCatalog.props, "product-catalog")
  val shoppingBasket = system.actorOf(ShoppingBasket.props(productCatalog), "shopping-basket")
  val productCatalogService = system.actorOf(ProductCatalogServiceActor.props, "product-catalog-service")
  val shoppingBasketService = system.actorOf(ShoppingBasketServiceActor.props(shoppingBasket), "shopping-basket-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(productCatalogService, interface = "localhost", port = 8080)
  IO(Http) ? Http.Bind(shoppingBasketService, interface = "localhost", port = 8081)
}
