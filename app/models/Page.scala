package models

import play.api.Play

case class Page(rows: Seq[Product], page: Int, limit: Long, total: Long)
