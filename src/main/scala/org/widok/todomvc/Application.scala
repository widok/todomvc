package org.widok.todomvc

import org.widok._
import org.widok.html._

object Application extends PageApplication {
  type Todo = Ref[Var[String]]
  case class Filter(value: String, f: Todo => ReadChannel[Boolean])

  val todo = Channel[String]()
  val todos = Buffer[Todo]()

  val completed = BufSet[Todo]()
  val uncompleted = todos - completed

  val editing = Var(Option.empty[Todo])

  val filterAll = Filter("All", _ => Var(true))
  val filterActive = Filter("Active", uncompleted.contains)
  val filterCompleted = Filter("Completed", completed.contains)

  val filters = Buffer(filterAll, filterActive, filterCompleted)
  val filter = Var(filterAll)

  todo.filterCycles.map(_.trim).filter(_.nonEmpty).attach { value =>
    todos += Ref(Var(value))
    todo := "" /* Makes the `filterCycles` call necessary. */
  }

  todos.removals.attach(completed -= _)

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
          .bind(Channel(uncompleted.isEmpty, (c: Boolean) => completed.toggle(c, todos: _*)))
          .show(todos.nonEmpty)
          .id("toggle-all")
          .cursor(cursor.Pointer)

        , label("Mark all as complete")
          .forId("toggle-all")

        , ul().bind(todos) { t =>
          li(
            div(
              checkbox()
                .bind(Channel(completed.contains(t), (c: Boolean) => completed.toggle(c, t)))
                .css("toggle")

            , label(t.get)
                .onDoubleClick(_ => editing := Some(t))

            , button()
                .onClick(_ => todos.remove(t))
                .css("destroy")
                .cursor(cursor.Pointer)
            ).css("view")

          , text()
              .bind(t.get)
              .attach(_ => editing := None)
              .css("edit")
              .show(editing.equal(Some(t)))
          ).css("todo")
           .cssCh(editing.equal(Some(t)), "editing")
           .cssCh(completed.contains(t), "completed")
           .show(filter.flatMap(_.f(t)))
        }.id("todo-list")
      ).id("main")

    , footer(
        div(b(uncompleted.size), " item(s) left")
          .id("todo-count")

      , ul().bind(filters) { f =>
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
    ).id("info")
  )

  def ready() {}
}