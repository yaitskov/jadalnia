import { Thenable } from 'async/abortable-promise';
import {OrderInfoCustomerView, OrderLabel, OrderPayResult } from 'app/types/order';
import { OrderProgress, OrderState, UpdateAttemptOutcome } from 'app/types/order';
import { RestSr } from "app/service/rest-service";
import { KelnerOrderView, OrderItem, OrderUpdate } from 'app/types/order';
import {Fid} from "app/page/festival/festival-types";
import { DishName } from 'app/types/menu';

export class OrderSr {
  // @ts-ignore
  private $restSr: RestSr;

  kelnerTakenOrderId(): Thenable<OrderLabel> {
    return this.$restSr.getS(`/api/order/executing`);
  }

  takeOrderForExec(): Thenable<OrderLabel> {
    return this.$restSr.postJ(`/api/order/try`,  {});
  }

  countOrdersReadyForExec(): Thenable<number> {
    return this.$restSr.getS(`/api/order/count-ready-for-exec`);
  }

  getInfo(orderLabel: OrderLabel): Thenable<KelnerOrderView> {
    return this.$restSr.getS(`/api/order/get/${orderLabel}`);
  }

  customerIsAbsent(orderLabel: OrderLabel): Thenable<void> {
    return this.$restSr.postJ(`/api/order/customer-missing/${orderLabel}`, {});
  }

  kelnerTired(orderLabel: OrderLabel): Thenable<void> {
    return this.$restSr.postJ(`/api/order/kelner-tired/${orderLabel}`, {});
  }

  markOrderReady(orderLabel: OrderLabel): Thenable<void> {
    return this.$restSr.postJ(`/api/order/ready/${orderLabel}`, {});
  }

  kelnerLacksFood(orderLabel: OrderLabel, dishName: DishName): Thenable<void> {
    return this.$restSr.postJ(`/api/order/low-food/${orderLabel}/${dishName}`, {});
  }

  customerPutOrder(items: OrderItem[]): Thenable<OrderLabel> {
    return this.$restSr.postJ('/api/order/put', items);
  }

  customerPaysOrder(orderLabel: OrderLabel): Thenable<OrderPayResult> {
    return this.$restSr.postJ(`/api/order/pay/${orderLabel}`, []);
  }

  customerCancelsOrder(orderLabel: OrderLabel): Thenable<OrderPayResult> {
    return this.$restSr.postJ(`/api/order/cancel/${orderLabel}`, []);
  }

  rescheduleOrder(orderLabel: OrderLabel): Thenable<OrderPayResult> {
    return this.$restSr.postJ(`/api/order/rescheduleAbandoned/${orderLabel}`, []);
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

  customerPicksOrder(orderLabel: OrderLabel): Thenable<void> {
    return this.$restSr.postJ(`/api/order/pickup/${orderLabel}`, [])
  }

  modifyOrder(orderUpdate: OrderUpdate): Thenable<UpdateAttemptOutcome> {
    return this.$restSr.postJ('/api/order/modify', orderUpdate)
  }

  listUnavailableMeals(): Thenable<DishName[]> {
    return this.$restSr.getS('/api/order/list-unavailable-meals-with-orders')
  }

  mealAvailable(meal: DishName): Thenable<void> {
    return this.$restSr.postJ(`/api/order/meal-available/${meal}`, [])
  }
}
