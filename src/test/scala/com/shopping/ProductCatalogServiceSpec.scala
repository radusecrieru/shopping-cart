package com.shopping

import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.testkit.Specs2RouteTest

class ProductCatalogServiceSpec extends Specification with Specs2RouteTest with ProductCatalogService {
  def actorRefFactory = system

  "ProductCatalogService" should {

    "return the product catalog for GET requests to the product-catalog path" in {
      Get("/product-catalog") ~> productCatalogRoute ~> check {
        responseAs[String] must contain("{\"id\":1,\"name\":\"Shoes\",\"price\":20.0}")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> productCatalogRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the product-catalog path" in {
      Put("/product-catalog") ~> sealRoute(productCatalogRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }

}
