package org.widok.todomvc.pages

import org.scalajs.dom
import org.widok._
import org.widok.bindings.HTML._

case class Todo(value: String, completed: Boolean = false, editing: Boolean = false)
case class Filter(value: String, f: Todo => Boolean)

case class Main() extends Page {
  val todo = Channel[String]()
  val todos = Aggregate[Todo]()
  val cachedTodos = todos.cache

  val filterAll = Filter("All", _ => true)
  val filterActive = Filter("Active", !_.completed)
  val filterCompleted = Filter("Completed", _.completed)

  val filters = Seq(filterAll, filterActive, filterCompleted)
  val filter = Channel[Filter]()
  val filtered = cachedTodos.filter(filter.map(_.f))

  val completed = todos.filter(cur => cur.completed)
  val uncompleted = todos.filter(!_.completed)
  val allCompleted = todos.forall(_.completed)

  def contents() = Seq(
    Section(
      Header(
        Heading.Level1("todos"),
        Input.Text(
          autofocus = true,
          autocomplete = false,
          placeholder = "What needs to be done?"
        ).bind(todo, (t: String) => if (t != "") {
          todos.append(Todo(t))
          todo := ""
        }, live = false)
          .withId("new-todo")
      ).withId("header"),

      Section(
        Input.Checkbox()
          .bind(allCompleted, (state: Boolean) => cachedTodos.update(cur => cur.copy(completed = state)))
          .withCursor(Cursor.Pointer)
          .withId("toggle-all")
          .show(todos.nonEmpty),

        Label(forId = "toggle-all")("Mark all as complete"),

        List.Unordered().bind(filtered) { todo =>
          val value = todo.lens[String](_.value, (cur, value) => cur.copy(value = value))
          val completed = todo.lens[Boolean](_.completed, (cur, value) => cur.copy(completed = value))
          val editing = todo.lens[Boolean](_.editing, (cur, value) => cur.copy(editing = value))

          val editField = Input.Text()
            .bind(value, (changed: String) => { value := changed; editing := false }, live = false)
            .withCSS("edit")

          List.Item(
            Container.Generic(
              Input.Checkbox()
                .bind(completed)
                .withCSS("toggle")
                .withCSS(completed, "completed"),

              Label()(value)
                .bindMouse(Event.Mouse.DoubleClick, (e: dom.MouseEvent) => editing := true),

              Button()
                .withCSS("destroy")
                .withCursor(Cursor.Pointer)
                .bindMouse(Event.Mouse.Click, (e: dom.MouseEvent) => filtered.remove(todo))
            ).withCSS("view"),

            editing.map(if (_) Some(editField) else None)
          ).withCSS(editing, "editing")
            .withCSS(completed, "completed")
        }.withId("todo-list")
      ).withId("main"),

      Footer(
        Container.Generic(Text.Bold(uncompleted.size), " item(s) left").withId("todo-count"),

        List.Unordered(filters.map(f =>
          List.Item(Anchor()(f.value)
            .bindMouse(Event.Mouse.Click, (e: dom.MouseEvent) => filter := f)
            .withCSS(filter.map(_ == f), "selected"))
        ): _*).withId("filters"),

        Button("Clear completed (", completed.size, ")")
          .show(completed.nonEmpty)
          .bindMouse(Event.Mouse.Click, (e: dom.MouseEvent) => completed.clear())
          .withCursor(Cursor.Pointer)
          .withId("clear-completed")
      ).show(todos.nonEmpty)
        .withId("footer")
    ).withId("todoapp"),

    Footer(
      Paragraph("Double-click to edit a todo"),
      Paragraph("Written by ", Anchor("http://github.com/tindzk/")("Tim Nieradzik")),
      Paragraph("Part of ", Anchor("http://todomvc.com/")("TodoMVC"))
    ).withId("info")
  )

  def ready(route: InstantiatedRoute) {
    filter := filterAll
  }
}