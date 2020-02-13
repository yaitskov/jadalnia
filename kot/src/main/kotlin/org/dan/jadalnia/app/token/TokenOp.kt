package org.dan.jadalnia.app.token

enum class TokenOp {
  Buy {
    override fun sign(): Int = 1
  },
  Sel {
    override fun sign(): Int = -1
  };

  abstract fun sign(): Int
}