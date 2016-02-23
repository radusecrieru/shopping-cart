package com.shopping

import akka.actor.{Actor, ActorRef, Props}
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.routing._
import com.shopping.ShoppingBasketItemJsonProtocol._
import spray.json._
import com.shopping.ShoppingBasket._

object ShoppingBasketServiceActor {
  def props(shoppingBasket: ActorRef): Props = Props(new ShoppingBasketServiceActor(shoppingBasket))
}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ShoppingBasketServiceActor(val shoppingBasketActor: ActorRef) extends Actor with ShoppingBasketService {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(shoppingBasketServiceRoute)
}

// this trait defines our service behavior independently from the service actor
trait ShoppingBasketService extends HttpService {

  val shoppingBasketActor: ActorRef

  val shoppingBasketServiceRoute =
    pathPrefix("shopping-basket") {
      pathEnd {
        get {
          respondWithMediaType(`application/json`) {
            complete {
              shoppingBasket.toJson.compactPrint
            }
          }
        }
      } ~
        path(IntNumber / "add-one") { itemId =>
          post {
            complete {
              shoppingBasketActor ! AddOne(itemId)
              OK
            }
          }
        } ~
        path(IntNumber / "remove-one") { itemId =>
          post {
            complete {
              shoppingBasketActor ! RemoveOne(itemId)
              OK
            }
          }
        } ~
        path(IntNumber / "remove-all") { itemId =>
          post {
            complete {
              shoppingBasketActor ! RemoveAll(itemId)
              OK
            }
          }
        }
    }
}

