package org.widok.todomvc

import org.widok._
import org.widok.html._

case class Todo(value: Var[String], completed: Var[Boolean] = Var(false), editing: Var[Boolean] = Var(false))
case class Filter(value: String, f: Todo => ReadChannel[Boolean])

object Main extends PageApplication {
  val todo = Channel[String]()
  val todos = Buffer[Todo]()

  todo.filterCycles.map(_.trim).filter(_.nonEmpty).attach { value =>
    todos += Todo(Var(value))
    todo := "" /* This must be used in conjunction with ``filterCycles``. */
  }

  val filterAll = Filter("All", _ => Var(true))
  val filterActive = Filter("Active", _.completed.map(!_))
  val filterCompleted = Filter("Completed", _.completed)

  val filters = Buffer(filterAll, filterActive, filterCompleted)
  val filter = Var(filterAll)

  val (completed, uncompleted) = todos.view(_.completed).partition(_.completed.get)

  def view() = Inline(
    section(
      header(
        h1("todos")
        , text()
          .bind(todo)
          .autofocus(true)
          .placeholder("What needs to be done?")
          .id("new-todo")
      ).id("header")

      , section(
        checkbox() /* All completed? */
          .bind(Channel(uncompleted.isEmpty,
            (checked: Boolean) => todos.foreach(_.completed := checked)))
          .show(todos.nonEmpty)
          .id("toggle-all")
          .cursor(cursor.Pointer)

        , label("Mark all as complete")
          .forId("toggle-all")

        , ul().bind(todos) { case tRef @ Ref(t) =>
          li(
            div(
              checkbox()
                .bind(t.completed)
                .css("toggle")

              , label(t.value)
                .onDoubleClick(_ => t.editing := true)

              , button()
                .onClick(_ => todos.remove(tRef))
                .css("destroy")
                .cursor(cursor.Pointer)
            ).css("view")

            , text()
              .bind(t.value)
              .attach(_ => t.editing := false)
              .css("edit")
              .show(t.editing)
          ).css("todo")
           .cssCh(t.editing, "editing")
           .cssCh(t.completed, "completed")
           .show(filter.flatMap(_.f(t)))
        }.id("todo-list")
      ).id("main")

      , footer(
        div(b(uncompleted.size), " item(s) left")
          .id("todo-count")

        , ul().bind(filters) { case Ref(f) =>
          li(
            a(f.value)
              .onClick(_ => filter := f)
              .cursor(cursor.Pointer)
              .cssCh(filter.equal(f), "selected")
          )
        }.id("filters")

        , button("Clear completed (", completed.size, ")")
          .onClick(_ => todos.removeAll(completed))
          .show(completed.nonEmpty)
          .cursor(cursor.Pointer)
          .id("clear-completed")
      ).show(todos.nonEmpty)
       .id("footer")
    ).id("todoapp")

    , footer(
      p("Double-click to edit a todo")
      , p("Written by ", a("Tim Nieradzik").url("http://github.com/tindzk/"))
      , p("Part of ", a("TodoMVC").url("http://todomvc.com/"))
    ).id("info")
  )

  def ready() {}
}