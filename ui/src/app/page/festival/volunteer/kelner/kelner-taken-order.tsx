import { h } from 'preact';
import { route } from 'preact-router';

import {TransCom, TransComS} from "i18n/trans-component";
import { DishName } from 'app/types/menu';
import {OrderLabel, KelnerOrderView, OrderItem} from "app/types/order";
import {Fid} from "app/page/festival/festival-types";
import {OrderSr} from "app/service/order-service";
import {RestErrCo} from "component/err/error";
import {Loading} from "component/loading";
import { T } from 'i18n/translate-tag';
import { jne } from 'collection/join-non-empty';

import bulma from "app/style/my-bulma.sass";

export interface KelnerTakenOrderP {
  fid: Fid;
  orderLbl: OrderLabel;
}

export interface KelnerTakenOrderS extends TransComS {
  e?: Error;
  orderInfo?: KelnerOrderView;
  showIssueTypes: boolean;
  missingMealToPick: OrderItem[];
}

export class KelnerTakenOrder extends TransCom<KelnerTakenOrderP, KelnerTakenOrderS> {
  // @ts-ignore
  $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at(), showIssueTypes: false, missingMealToPick: []};
    this.orderPickedUpByCustomer = this.orderPickedUpByCustomer.bind(this);
    this.cannotExecOrder = this.cannotExecOrder.bind(this);
    this.customerLate = this.customerLate.bind(this);
    this.lowFood = this.lowFood.bind(this);
    this.pickMissingMeal = this.pickMissingMeal.bind(this);
  }

  wMnt() {
    this.$orderSr.getInfo(this.pr.orderLbl).tn(ordInfo => {
      this.ust(st => ({...st, orderInfo: ordInfo}));
    }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  cannotExecOrder() {
    this.ust(st => ({...st, showIssueTypes: true}));
  }

  orderPickedUpByCustomer() {
    this.$orderSr.markOrderReady(this.pr.orderLbl).tn(ok => {
      route(`/festival/kelner/serve/${this.pr.fid}`);
    }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  customerLate() {
    this.$orderSr.customerIsAbsent(this.pr.orderLbl).tn(ok => {
      route(`/festival/kelner/serve/${this.pr.fid}`);
    }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  pickMissingMeal(meal: DishName) {
    this.$orderSr.kelnerLacksFood(this.pr.orderLbl, meal).tn(ok => {
      route(`/festival/kelner/serve/${this.pr.fid}`);
    }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  lowFood(orderInfo: KelnerOrderView) {
    if (orderInfo.items.length == 1) {
      this.pickMissingMeal(orderInfo.items[0].name);
    } else {
      this.ust(st => ({...st, missingMealToPick: orderInfo.items}));
    }
  }

  kelnerNeedRest() {
    this.$orderSr.kelnerTired(this.pr.orderLbl).tn(ok => {
      route(`/festival/kelner/serve/${this.pr.fid}`);
    }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [LoadingI, TI] = this.c2(Loading, T);
    return <div>
      <RestErrCo e={st.e} />
      {!st.e && !st.orderInfo && <LoadingI/>}
      {!st.e && !!st.orderInfo && <div>
        <div class={bulma.buttons}>
            { !st.showIssueTypes && <div>
              <button class={jne(bulma.button, bulma.isWarning)}
                      onClick={this.cannotExecOrder}>
                <TI m="order issue"/>
              </button>
              <button class={jne(bulma.button, bulma.isPrimary)}
                      onClick={this.orderPickedUpByCustomer}>
                <TI m="order picked up"/>
              </button>
            </div>}
            { st.showIssueTypes && <div>
              { !!st.missingMealToPick.length && <div>
                {st.missingMealToPick.map(item =>
                  <button class={bulma.button} onClick={() => this.pickMissingMeal(item.name)}>
                    <TI m="meal is not available now" meal={item.name} />
                  </button>
                )}
              </div>}
              { !st.missingMealToPick.length && <div>
                <button class={jne(bulma.button, bulma.isWarning)}
                        onClick={this.customerLate}>
                  <TI m="customer late"/>
                </button>
                <button class={jne(bulma.button, bulma.isDanger)}
                        onClick={() => this.lowFood(st.orderInfo!!)}>
                  <TI m="low food"/>
                </button>
                <button class={jne(bulma.button, bulma.isPrimary)}
                        onClick={this.kelnerNeedRest}>
                  <TI m="need rest"/>
                </button>
              </div>}
            </div>}
        </div>
        <p>
          <TI m="Order includes" lbl={p.orderLbl}/>
        </p>
        <ul>
          {!!st.orderInfo && st.orderInfo.items.map(
            item => <li>{item.quantity} * {item.name}</li>)
          }
        </ul>
      </div>}
    </div>;
  }

  at() { return []; }
}
