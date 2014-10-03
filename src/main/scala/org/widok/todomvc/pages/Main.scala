package org.widok.todomvc.pages

import org.widok._
import org.widok.bindings.HTML._

case class Todo(value: String, completed: Boolean = false, editing: Boolean = false)

case class Filter(value: String, f: Todo => Boolean)

case class Main() extends Page {
  val addTodo = Channel[String]()

  val todos = Aggregate[Todo]()
  val cachedTodos = todos.cache

  val filterAll = Filter("All", _ => true)
  val filterActive = Filter("Active", !_.completed)
  val filterCompleted = Filter("Completed", _.completed)

  val filter = Channel[Filter]()
  val filtered = cachedTodos.filter(filter.map(_.f))

  val filters = Channel[Seq[Filter]]()

  addTodo.attach(t => if (!t.isEmpty) {
    todos.append(Todo(t))
    addTodo := ""
  })

  val completed = todos.filter(_.completed)
  val uncompleted = todos.filter(!_.completed)
  val allDone = todos.forall(_.completed)
  val cachedAllDone = allDone.cache

  def contents() = Seq(
    Section(
      Header(
        Heading.Level1("todos"),
        Input.Text(
          autofocus = true,
          autocomplete = false,
          placeholder = "What needs to be done?"
        )
          .bind(addTodo)
          .withId("new-todo")
      ).withId("header"),

      Section(
        Input.Checkbox()
          .bind(allDone)
          .withCursor(Cursor.Pointer)
          .withId("toggle-all")
          .onClick(() => cachedTodos.update(cur => cur.copy(completed = cachedAllDone.get.get))),

        Label(forId = "toggle-all")("Mark all as complete"),

        List.Unordered().bind(filtered, (todo: Channel[Todo], li: List.Item) => {
          val value = todo.lens[String](_.value, (cur, value) => cur.copy(value = value))
          val completed = todo.lens[Boolean](_.completed, (cur, value) => cur.copy(completed = value))
          val editing = todo.lens[Boolean](_.editing, (cur, value) => cur.copy(editing = value))

          editing.attach(value => li.setCSS("editing", value))
          completed.attach(value => li.setCSS("completed", value))

          val editField = Input.Text()
            .bind(value)
            .onEnter(() => editing := false)
            .withCSS("edit")

          Container.Generic(
            Container.Generic(
              Input.Checkbox()
                .bind(completed, (completed: Boolean, box: Input.Checkbox) => box.setCSS("completed", completed))
                .withCSS("toggle"),

              Label()(value).onDoubleClick(() => editing := true),

              Button()
                .withCSS("destroy")
                .withCursor(Cursor.Pointer)
                .onClick(() => filtered.remove(todo))
            ).withCSS("view"),

            editing.bind { case true => editField }
          )
        }).withId("todo-list")
      ).withId("main"),

      Footer(
        Container.Generic(Text.Bold(uncompleted.size), " item(s) left").withId("todo-count"),

        List.Unordered().bind(filters, (f: Filter, li: List.Item) => {
          val elem = Anchor()(f.value).onClick(() => filter := f)
          filter.attach(newFilter => elem.setCSS("selected", newFilter == f))
          elem
        }).withId("filters"),

        Button("Clear completed (", completed.size, ")")
          .show(completed.nonEmpty)
          .onClick(() => completed.clear())
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
    filters := Seq(filterAll, filterActive, filterCompleted)
    filter := filterAll
  }
}