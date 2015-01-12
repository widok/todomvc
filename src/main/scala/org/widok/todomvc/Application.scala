package org.widok.todomvc

import org.widok._
import org.widok.bindings.HTML._

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
    Section(
      Header(
        Heading.Level1("todos"),
        Input.Text()
          .bind(todo)
          .autofocus(true)
          .placeholder("What needs to be done?")
          .id("new-todo")
      ).id("header"),

      Section(
        Input.Checkbox() /* All completed? */
          .bind(Channel(uncompleted.isEmpty,
            (checked: Boolean) => todos.foreach(_.completed := checked)))
          .show(todos.nonEmpty)
          .id("toggle-all")
          .cursor(Cursor.Pointer),

        Label("Mark all as complete")
          .forId("toggle-all"),

        List.Unordered().bind(todos) { case tRef @ Ref(t) =>
          List.Item(
            Container.Generic(
              Input.Checkbox()
                .bind(t.completed)
                .css("toggle"),

              Label(t.value)
                .onDoubleClick(_ => t.editing := true),

              Button()
                .onClick(_ => todos.remove(tRef))
                .css("destroy")
                .cursor(Cursor.Pointer)
            ).css("view"),

            Input.Text()
              .bind(t.value)
              .attach(_ => t.editing := false)
              .css("edit")
              .show(t.editing)
          ).css("todo")
           .cssCh(t.editing, "editing")
           .cssCh(t.completed, "completed")
           .show(filter.flatMap(_.f(t)))
        }.id("todo-list")
      ).id("main"),

      Footer(
        Container.Generic(Text.Bold(uncompleted.size), " item(s) left")
          .id("todo-count"),

        List.Unordered().bind(filters) { case Ref(f) =>
          List.Item(
            Anchor(f.value)
              .onClick(_ => filter := f)
              .cursor(Cursor.Pointer)
              .cssCh(filter.equal(f), "selected")
          )
        }.id("filters"),

        Button("Clear completed (", completed.size, ")")
          .onClick(_ => todos.removeAll(completed))
          .show(completed.nonEmpty)
          .cursor(Cursor.Pointer)
          .id("clear-completed")
      ).show(todos.nonEmpty)
       .id("footer")
    ).id("todoapp"),

    Footer(
      Paragraph("Double-click to edit a todo"),
      Paragraph("Written by ", Anchor("Tim Nieradzik").url("http://github.com/tindzk/")),
      Paragraph("Part of ", Anchor("TodoMVC").url("http://todomvc.com/"))
    ).id("info")
  )

  def ready() {}
}