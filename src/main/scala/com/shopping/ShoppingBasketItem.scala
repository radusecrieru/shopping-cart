package com.shopping

import spray.json._

case class ShoppingBasketItem(id: Int, quantity: Int) {
}

object ShoppingBasketItemJsonProtocol extends DefaultJsonProtocol {
  implicit val shoppingBasketItem = jsonFormat2(ShoppingBasketItem)
}