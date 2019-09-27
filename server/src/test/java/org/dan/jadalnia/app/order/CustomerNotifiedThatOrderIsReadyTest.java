package org.dan.jadalnia.app.order;

import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.junit.Test;

/**
 * kelner marks order as ready
 * customer is notified by websocket that order is ready
 *  (customer is notified about ready for pick up orders
 *   on websocket reconnection also)
 * kelner becomes free
 */
public class CustomerNotifiedThatOrderIsReadyTest {
    public static Boolean markAsReady(
            MyRest myRest, OrderLabel order, UserSession session) {
        return myRest.post(OrderResource.ORDER_READY,
                session, order, Boolean.class);
    }

    @Test
    public void kelnerMarksOrderAsReadyForPickup() {

    }
}
