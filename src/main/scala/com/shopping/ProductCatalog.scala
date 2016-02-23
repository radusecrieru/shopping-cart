package com.shopping

import akka.actor.{Actor, Props}
import com.shopping.ProductCatalog._


object ProductCatalog {
  var productCatalog = List(ProductCatalogItem(Product(1, "Shoes", 20.0), 3), ProductCatalogItem(Product(2, "Shirt", 15.5), 14), ProductCatalogItem(Product(3, "Hat", 10.2), 8))

  def props(): Props = Props(new ProductCatalog)

  case class Retrieve(id: Int, quantity: Int) {}
  case class AddBack(id: Int, quantity: Int) {}
}

class ProductCatalog extends Actor {

  def receive = {
    case Retrieve(id, q) =>
      productCatalog =
        productCatalog.map(item =>
          if(item.product.id == id)
            if (item.quantity < q) throw new IllegalArgumentException else ProductCatalogItem(item.product, item.quantity - q)
          else item)

    case AddBack(id, q) =>
      productCatalog =
        productCatalog.map(item => if(item.product.id == id) ProductCatalogItem(item.product, item.quantity + q) else item)

  }
}

