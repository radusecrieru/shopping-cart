package com.shopping

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import com.shopping.ShoppingBasket._
import com.shopping.ProductCatalog._

import scala.concurrent.duration._

object ShoppingBasket {
  var shoppingBasket = List[ShoppingBasketItem]()

  def props(productCatalog: ActorRef): Props = Props(new ShoppingBasket(productCatalog))

  case class AddOne(id: Int) {}

  case class RemoveOne(id: Int) {}

  case class RemoveAll(id: Int) {}

}

class ShoppingBasket(productCatalog: ActorRef) extends Actor {

  implicit val timeout = Timeout(10 seconds) // needed for `?` below

  def receive = {
    case AddOne(id) => {
      if (ProductCatalog.productCatalog.find({ item => item.product.id == id }).forall(item => item.quantity > 0)) {
        val item = shoppingBasket.find({ item => item.id == id }).getOrElse(ShoppingBasketItem(id, 0))
        shoppingBasket = ShoppingBasketItem(item.id, item.quantity + 1) :: shoppingBasket.filterNot({ item => item.id == id })
        productCatalog ! Retrieve(id, 1)
      }
    }

    case RemoveOne(id) => {
      if (shoppingBasket.find({ item => item.id == id }).getOrElse(ShoppingBasketItem(id, 0)).quantity > 0) {
        shoppingBasket = shoppingBasket.map({ item => if (item.id == id) ShoppingBasketItem(id, item.quantity - 1) else item }).filter({ case item => item.quantity > 0 })
        productCatalog ! AddBack(id, 1)
      }
    }

    case RemoveAll(id) => {
      val quantity: Int = shoppingBasket.find({ item => item.id == id }).getOrElse(ShoppingBasketItem(id, 0)).quantity
      shoppingBasket = shoppingBasket.filterNot({ item => item.id == id })
      productCatalog ! AddBack(id, quantity)
    }
  }
}

