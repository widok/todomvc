package org.widok.todomvc

import org.scalajs.dom
import org.widok._
import org.widok.bindings.HTML._

case class Todo(value: String, completed: Boolean = false, editing: Boolean = false)
case class Filter(value: String, f: Todo => Boolean)

object Main extends PageApplication {
  val todo = Channel[String]()
  val todos = VarBuf[Todo]()
  todo.map(_.trim).filter(_.nonEmpty).attach { value =>
    todos += Todo(value)
    todo := ""
  }

  val filterAll = Filter("All", _ => true)
  val filterActive = Filter("Active", !_.completed)
  val filterCompleted = Filter("Completed", _.completed)

  val filters = Seq(filterAll, filterActive, filterCompleted)
  val filter = Var(filterAll)
  val filtered = todos.filterCh(filter.map(_.f))

  val (completed, uncompleted) = todos.partition(_.completed)
  val allCompleted = uncompleted.isEmpty

  def contents() = Seq(
    Section(
      Header(
        Heading.Level1("todos"),
        Input.Text()
          .autofocus(true)
          .placeholder("What needs to be done?")
          .bind(todo)
          .id("new-todo")
      ).id("header"),

      Section(
        Input.Checkbox()
          .bind(allCompleted.writeTo(todos.setter[Boolean](_ >> 'completed)))
          .show(todos.nonEmpty)
          .id("toggle-all")
          .cursor(Cursor.Pointer),

        Label("Mark all as complete")
          .forId("toggle-all"),

        List.Unordered().bind(filtered) { todo =>
          val value = todo.value[String](_ >> 'value)
          val completed = todo.value[Boolean](_ >> 'completed)
          val editing = todo.value[Boolean](_ >> 'editing)

          List.Item(
            Container.Generic(
              Input.Checkbox()
                .bind(completed)
                .css("toggle")
                .cssCh(completed, "completed"),

              Label(value)
                .bindMouse(Event.Mouse.DoubleClick, (e: dom.MouseEvent) => editing := true),

              Button()
                .bind((_: Unit) => todos.remove(todo))
                .css("destroy")
                .cursor(Cursor.Pointer)
            ).css("view"),

            Input.Text()
              .bind(value + ((_: String) => editing := false))
              .css("edit")
              .show(editing)
          ).cssCh(editing, "editing")
           .cssCh(completed, "completed")
        }.id("todo-list")
      ).id("main"),

      Footer(
        Container.Generic(Text.Bold(uncompleted.size), " item(s) left")
          .id("todo-count"),

        List.Unordered().bind(Var(filters)) { f =>
          List.Item(
            Anchor(f.value)
              .bind((_: Unit) => filter := f)
              .cursor(Cursor.Pointer)
              .cssCh(filter.equal(f), "selected"))
        }.id("filters"),

        Button("Clear completed (", completed.size, ")")
          .bind((_: Unit) => todos.removeAll(completed))
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