import {Fid} from "app/page/festival/festival-types";
import {RestSr} from "app/service/rest-service";
import {Thenable} from 'async/abortable-promise';
import {Tobj} from "collection/typed-object";
import { TokenPoints } from "app/types/token";

export interface KelnerPerformanceRow {
  name: String;
  orders: number;
  tokens: TokenPoints;
}

export interface CashierPerformanceRow {
  name: String;
  requests: number;
  tokens: TokenPoints;
}

export class PerformanceSr {
  // @ts-ignore
  private $restSr: RestSr;

  kelnerPerformance(fid: Fid): Thenable<KelnerPerformanceRow[]> {
    return this.$restSr.geT(`/api/performance/kelner/${fid}`);
  }

  cashierPerformance(fid: Fid): Thenable<CashierPerformanceRow[]> {
    return this.$restSr.geT(`/api/performance/cashier/${fid}`);
  }
}
