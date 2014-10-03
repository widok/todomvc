package org.widok.todomvc

import org.widok._

object Routes {
  val main = Route("/", pages.Main())
  val notFound = Route("/404", pages.NotFound())

  val routes = Set(main, notFound)
}

object Main extends Application(Routes.routes, Routes.notFound)