package org.dan.jadalnia.app.order

import java.util.LinkedList

class OpLog {
  var operations: LinkedList<() -> Unit> = LinkedList()

  fun <E: Throwable>rollback(e: E): E {
    rollback()
    return e
  }

  fun rollback() {
    val ops = operations
    operations = LinkedList()
    ops.forEach { operation -> operation() }
  }

  fun add(op: () -> Unit): OpLog {
    operations.push { op }
    return this
  }
}