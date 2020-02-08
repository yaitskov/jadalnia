package org.dan.jadalnia.app.order

enum class UpdateAttemptOutcome {
  UPDATED,
  FESTIVAL_OVER,
  BAD_ORDER_STATE,
  NOT_ENOUGH_FUNDS,
  RETRY
}