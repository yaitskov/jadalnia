import { Thenable } from 'async/abortable-promise';
import { OrderLabel } from 'app/types/order';
import { RestSr } from "app/service/rest-service";
import {KelnerOrderView, OrderItem } from 'app/types/order';

export class OrderSr {
  // @ts-ignore
  private $restSr: RestSr;

  kelnerTakenOrderId(): Thenable<OrderLabel> {
    return this.$restSr.getS(`/api/order/executing`);
  }

  takeOrderForExec(): Thenable<OrderLabel> {
    return this.$restSr.postJ(`/api/order/try`, '');
  }

  countOrdersReadyForExec(): Thenable<number> {
    return this.$restSr.getS(`/api/order/count-ready-for-exec`);
  }

  getInfo(orderLabel: OrderLabel): Thenable<KelnerOrderView> {
    return this.$restSr.getS(`/api/order/get/${orderLabel}`);
  }

  markOrderReady(orderLabel: OrderLabel): Thenable<void> {
    return this.$restSr.postJ('/api/order/ready', orderLabel);
  }

  customerPutOrder(items: OrderItem[]): Thenable<OrderLabel> {
    return this.$restSr.postJ('/api/order/put', items);
  }
}
