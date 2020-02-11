import {Fid} from "app/page/festival/festival-types";
import {RestSr} from "app/service/rest-service";
import {Thenable} from 'async/abortable-promise';
import {Tobj} from "collection/typed-object";

export type Dish2Quantity = Tobj<number>;

export interface MealDemandResponse {
  meals: Dish2Quantity;
}

export class OrderStatsSr {
  // @ts-ignore
  private $restSr: RestSr;

  demandPaid(fid: Fid): Thenable<MealDemandResponse> {
    return this.$restSr.geT(`/api/order-stats/demand/${fid}`);
  }
}
