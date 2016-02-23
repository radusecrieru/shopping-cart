package com.shopping

import spray.json._

case class Product(id: Int, name:String, price:Double) {
}
object ProductJsonProtocol extends DefaultJsonProtocol {
  implicit val product = jsonFormat3(Product)
}