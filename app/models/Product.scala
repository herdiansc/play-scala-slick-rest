package models

import play.api.Play
import javax.inject.Inject
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/*
 * Product Case Class
**/
case class Product(id:Option[Long], title: String, description: Option[String], price:Option[Long])

/*
 * Product Table Definition
**/
class ProductTable(tag: Tag) extends Table[Product](tag, "products") {
    def id = column[Option[Long]]("id", O.PrimaryKey,O.AutoInc)
    def title = column[String]("title")
    def description = column[Option[String]]("description")
    def price = column[Option[Long]]("price")

    def * = (id, title, description, price) <> ((Product.apply _).tupled, Product.unapply)
}

object Product {
    implicit val productRead: Reads[Product] = (
        (JsPath \ "id").readNullable[Long] and
        (JsPath \ "title").read[String](minLength[String](3)) and 
        (JsPath \ "description").readNullable[String] and 
        (JsPath \ "price").readNullable[Long]
    )(Product.apply _)

    implicit val productWrite: Writes[Product] = (
        (JsPath \ "id").write[Option[Long]] and
        (JsPath \ "title").write[String] and 
        (JsPath \ "description").write[Option[String]] and 
        (JsPath \ "price").write[Option[Long]]
    )(unlift(Product.unapply))
}
