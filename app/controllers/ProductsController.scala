package controllers

import play.api.libs.json._
import play.api.mvc._
import javax.inject.Inject
import scala.concurrent.Future
import models.{Product, ProductDAO, Page}
import scala.concurrent.ExecutionContext.Implicits.global

class ProductsController @Inject() (productDao: ProductDAO) extends Controller {

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
            data match {
                case Some(product) => Ok(Json.toJson(product)).as("application/json")
                case None => UnprocessableEntity
            }
        }
    }
    
    def update(id: Long) = Action.async(parse.json) { request => 
        request.body.validate[Product].map { product =>
            productDao.update(id, product).map { result =>
                result match {
                    case true => Ok(Json.obj("status" -> "success")).as("application/json")
                    case false => UnprocessableEntity
                }
            }.recoverWith {
                case e => Future { InternalServerError("ERROR: " + e ) }
            }
        }.recoverTotal {
            e => Future { BadRequest( JsError.toFlatJson(e) ) }
        }
    }
    
    def delete(id: Long) = Action.async { request =>
        productDao.delete(id).map { result =>
            result match {
                case true => Ok(Json.obj("status" -> "success")).as("application/json")
                case false => UnprocessableEntity
            }
        }.recoverWith {
            case e => Future { InternalServerError("ERROR: " + e ) }
        }
    }
}
