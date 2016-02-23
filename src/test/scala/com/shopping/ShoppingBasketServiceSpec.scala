package com.shopping

import akka.actor.ActorRef
import akka.testkit.TestActorRef
import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.testkit.Specs2RouteTest

class ShoppingBasketServiceSpec extends Specification with Specs2RouteTest with ShoppingBasketService {
  def actorRefFactory = system

  val productCatalogActor: ActorRef = TestActorRef(ProductCatalog.props)
  override val shoppingBasketActor: ActorRef = TestActorRef(ShoppingBasket.props(productCatalogActor))

  sequential

  "ShoppingBasketService" should {

    "return the shopping basket for GET requests to the shopping-basket path" in {
      Get("/shopping-basket") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("[]")
      }
    }

    "return the shopping basket for GET requests to the shopping-basket path after adding an item" in {
      Post("/shopping-basket/1/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Get("/shopping-basket") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("[{\"id\":1,\"quantity\":1}]")
      }
    }

    "return the shopping basket for GET requests to the shopping-basket path after adding three items" in {
      Post("/shopping-basket/1/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Get("/shopping-basket") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("{\"id\":1,\"quantity\":2}")
        responseAs[String] must contain("{\"id\":3,\"quantity\":2}")
      }
    }

    "return the shopping basket for GET requests to the shopping-basket path after adding multiple items and removing an item" in {

      Post("/shopping-basket/2/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/2/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/2/remove-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Get("/shopping-basket") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("{\"id\":1,\"quantity\":2}")
        responseAs[String] must contain("{\"id\":2,\"quantity\":1}")
        responseAs[String] must contain("{\"id\":3,\"quantity\":5}")
      }
    }

    "return the shopping basket for GET requests to the shopping-basket path after adding multiple items and removing all quantity from an item" in {

      Post("/shopping-basket/1/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/2/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/1/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/3/remove-all") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Get("/shopping-basket") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("{\"id\":1,\"quantity\":3}")
        responseAs[String] must contain("{\"id\":2,\"quantity\":2}")
      }
    }

    "return the shopping basket for GET requests to the shopping-basket path after adding two items with the same id and removing them" in {
      Post("/shopping-basket/1/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/1/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/1/add-one") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Post("/shopping-basket/1/remove-all") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Get("/shopping-basket") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("{\"id\":2,\"quantity\":2}")
      }
    }

    "return the empty shopping basket for GET requests to the shopping-basket path removing the remaining items" in {
      Post("/shopping-basket/2/remove-all") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("OK")
      }
      Get("/shopping-basket") ~> shoppingBasketServiceRoute ~> check {
        responseAs[String] must contain("[]")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> shoppingBasketServiceRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the product-catalog path" in {
      Put("/shopping-basket") ~> sealRoute(shoppingBasketServiceRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }

}
