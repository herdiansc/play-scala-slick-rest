package controllers

import play.api.libs.json._
import play.api.mvc._
import javax.inject.Inject
import scala.concurrent.Future
import models.{Product, ProductDAO, Page}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.functional.syntax._

class ProductsController @Inject() (productDao: ProductDAO) extends Controller {

    implicit val productRead: Reads[Product] = (
        (JsPath \ "id").readNullable[Long] and
        (JsPath \ "title").readNullable[String] and 
        (JsPath \ "description").readNullable[String] and 
        (JsPath \ "price").readNullable[Long]
    )(Product.apply _)

    implicit val productFormat: Writes[Product] = (
        (JsPath \ "id").write[Option[Long]] and
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

    def create = Action.async(parse.json) { request => 
        request.body.validate[Product].map { product =>
            productDao.insert(product).map {
                result => Created(Json.obj("status" -> "success")).as("application/json")
            }.recoverWith {
                case e => Future { InternalServerError("ERROR: " + e )}
            }
        }.recoverTotal {
            e => Future { BadRequest( JsError.toFlatJson(e) ) }
        }
    }

    def read(id: Long) = Action.async { request =>
        productDao.view(id).map { data => 
            Ok(Json.toJson(data)).as("application/json")
        }
    }
}
