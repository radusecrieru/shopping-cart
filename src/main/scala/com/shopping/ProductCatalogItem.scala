package com.shopping

import com.shopping.ProductJsonProtocol._
import spray.json._

case class ProductCatalogItem(product: Product, quantity: Int) {
}

object ProductCatalogItemJsonProtocol extends DefaultJsonProtocol {
  implicit val productCatalogItem = jsonFormat2(ProductCatalogItem)
}