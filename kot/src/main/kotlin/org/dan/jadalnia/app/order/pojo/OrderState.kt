package org.dan.jadalnia.app.order.pojo;

enum class OrderState {
    Accepted,
    Paid,
    Executing,
    Ready,
    Handed,
    Cancelled,
    Returned,
    Abandoned
}
