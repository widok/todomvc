package org.widok.todomvc.pages

import org.widok._
import org.widok.bindings.HTML._

case class NotFound() extends Page {
  def contents() = Heading.Level1("Not found!")
  def ready(route: InstantiatedRoute) { }
}
