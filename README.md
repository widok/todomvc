# TodoMVC
TodoMVC implementation for Widok.

Currently, it is the shortest available TodoMVC implementation (~120 lines).

## Installation
Compile Widok:

```
git clone git@github.com:widok/widok.git
cd widok
sbt publish-local
cd ..
```

Compile TodoMVC:
```
git clone git@github.com:widok/todomvc.git
cd todomvc
sbt fastOptJS
```

Now open ``application.html`` in the browser.

## See also
* [TodoMVC's official page](http://todomvc.com/)
* [Scala.JS implementation using scalatags](https://github.com/lihaoyi/workbench-example-app/blob/todomvc/src/main/scala/example/ScalaJSExample.scala)

## License
TodoMVC is licensed under the terms of the GPLv3.

## Authors
Tim Nieradzik <tim@kognit.io>