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
 * Product Case Class
**/
case class Product(id:Long, title: Option[String], description: Option[String], price:Option[Long])

/*
 * Product Table Definition
**/
class ProductTable(tag: Tag) extends Table[Product](tag, "products") {
    def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
    def title = column[Option[String]]("title")
    def description = column[Option[String]]("description")
    def price = column[Option[Long]]("price")

    def * = (id, title, description, price) <> ((Product.apply _).tupled, Product.unapply)
}

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

    def list(page: Int = 1, limit: Int = 10): Future[Page] = {
        val offset = limit * (page-1)

        for {
          totalRows <- count()
          list = products.drop(offset).take(limit).result
          result <- db.run(list)
        } yield Page(result, page, limit, totalRows)
    }
}
