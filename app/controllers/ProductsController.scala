package controllers

import play.api.libs.json._
import play.api.mvc._
import javax.inject.Inject
import models.{Product, ProductDAO, Page}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.functional.syntax._

class ProductsController @Inject() (productDao: ProductDAO) extends Controller {

    implicit val productFormat: Writes[Product] = (
        (JsPath \ "id").write[Long] and
        (JsPath \ "title").write[Option[String]] and 
        (JsPath \ "description").write[Option[String]] and 
        (JsPath \ "price").write[Option[Long]]
    )(unlift(Product.unapply))

    implicit val pageFormat: Writes[Page] = (
        (JsPath \ "rows").write[Seq[Product]] and 
        (JsPath \ "page").write[Int] and 
        (JsPath \ "limit").write[Long] and 
        (JsPath \ "total").write[Long]
    )(unlift(Page.unapply))

	def index(page: Int, limit:Int) = Action.async { request =>
        productDao.list(page = page, limit = limit).map { data => 
            Ok(Json.toJson(data)).as("application/json")
	    }
    }

    def read(id: Long) = Action.async { request =>
        productDao.view(id).map { data => 
            Ok(Json.toJson(data)).as("application/json")
        }
    }
}
