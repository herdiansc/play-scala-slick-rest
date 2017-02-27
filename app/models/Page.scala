package models

import play.api.Play

// CURRENT: Page rows type is hardcoded with Seq[Product]
// TODO: Page rows type should be generic with Seq[A] so that type will dynamic and can be used by another resource
case class Page(rows: Seq[Product], page: Int, limit: Long, total: Long)
