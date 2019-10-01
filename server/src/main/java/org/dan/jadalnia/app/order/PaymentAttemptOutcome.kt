package org.dan.jadalnia.app.order

enum class PaymentAttemptOutcome {
  NOT_ENOUGH_FUNDS,
  FUNDS_PENDING_APPROVAL,
  ALREADY_PAID,
  CANCELLED,
  RETRY,
  ORDER_PAID,
  FESTIVAL_OVER
}