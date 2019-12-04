import { Thenable } from 'async/abortable-promise';
import {OrderInfoCustomerView, OrderLabel, OrderPayResult, OrderProgress} from 'app/types/order';
import { RestSr } from "app/service/rest-service";
import { KelnerOrderView, OrderItem } from 'app/types/order';
import {Fid} from "app/page/festival/festival-types";

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

  customerPaysOrder(orderLabel: OrderLabel): Thenable<OrderPayResult> {
    return this.$restSr.postJ(`/api/order/pay/${orderLabel}`, []);
  }

  seeOrderProgress(fid: Fid, orderLabel: OrderLabel): Thenable<OrderProgress> {
    return this.$restSr.geT(`/api/order/progress/${fid}/${orderLabel}`);
  }

  getInfoForVisitor(orderLabel: OrderLabel): Thenable<OrderInfoCustomerView> {
    return this.$restSr.getS(`/api/order/customerInfo/${orderLabel}`);
  }

  customerLists(): Thenable<OrderInfoCustomerView> {
    return this.$restSr.getS(`/api/order/listMine`);
  }
}
