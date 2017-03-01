package models

//import play.api.libs.json._
import play.api.Play
import javax.inject.Inject
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/*
 * Product Data Access Object
**/
class ProductDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

    val products = TableQuery[ProductTable]

    def view(id: Long): Future[Option[Product]] = {
        db.run(products.filter(_.id === id).result.headOption)
    }

    def count(): Future[Int] = {
        db.run(products.map(_.id).length.result)
    }
    
    // TODO: add filtering functionality
    def list(page: Int = 1, limit: Int = 10): Future[Page] = {
        val offset = limit * (page-1)

        for {
          totalRows <- count()
          list = products.drop(offset).take(limit).result
          result <- db.run(list)
        } yield Page(result, page, limit, totalRows)
    }
    
    def insert(product: Product): Future[Unit] = db.run(products += product).map(_ => "Success")
    
    // TODO:
    // - update only present field
    def update(id: Long, product: Product): Future[Boolean] = {
        val newProduct: Product = product.copy(Some(id))
        db.run(products.filter(_.id === id).update(newProduct)).map { affectedRows => 
            affectedRows > 0
        }
    }
    
    def delete(id: Long): Future[Boolean] = {
        db.run(products.filter(_.id === id).delete).map { affectedRows =>
            affectedRows > 0
        }
    }
}
