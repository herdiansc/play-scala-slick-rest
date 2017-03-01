package models

import play.api.Play
import play.api.libs.json._
import play.api.libs.functional.syntax._

// CURRENT: Page rows type is hardcoded with Seq[Product]
// TODO: Page rows type should be generic with Seq[A] so that type will dynamic and can be used by another resource
case class Page(rows: Seq[Product], page: Int, limit: Long, total: Long)

object Page {
    implicit val pageWrite: Writes[Page] = (
        (JsPath \ "rows").write[Seq[Product]] and 
        (JsPath \ "page").write[Int] and 
        (JsPath \ "limit").write[Long] and 
        (JsPath \ "total").write[Long]
    )(unlift(Page.unapply))
}
