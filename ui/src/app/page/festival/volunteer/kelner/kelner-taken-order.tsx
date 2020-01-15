import { h } from 'preact';
import { route } from 'preact-router';

import {TransCom, TransComS} from "i18n/trans-component";
import {OrderLabel, KelnerOrderView} from "app/types/order";
import {Fid} from "app/page/festival/festival-types";
import {OrderSr} from "app/service/order-service";
import {RestErrCo} from "component/err/error";
import {Loading} from "component/loading";
import { T } from 'i18n/translate-tag';

import bulma from "app/style/my-bulma.sass";

export interface KelnerTakenOrderP {
  fid: Fid;
  orderLbl: OrderLabel;
}

export interface KelnerTakenOrderS extends TransComS {
  e?: Error;
  orderInfo?: KelnerOrderView;
  showIssueTypes: boolean;
}

export class KelnerTakenOrder extends TransCom<KelnerTakenOrderP, KelnerTakenOrderS> {
  // @ts-ignore
  $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at(), showIssueTypes: false};
    this.orderPickedUpByCustomer = this.orderPickedUpByCustomer.bind(this);
    this.cannotExecOrder = this.cannotExecOrder.bind(this);
    this.customerLate = this.customerLate.bind(this);
    this.lowFood = this.lowFood.bind(this);
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
    this.$orderSr.customerIsAbsent(this.pr.orderLbl);

    // customer is missing and cannot pick up the order
    // reschedule the order
  }

  lowFood() {
    // food for require item is not ready yet.
    // postpone and serve asap once ready
  }

  kelnerNeedRest() {
    // kelner rejects executing order right now due
    // personal reason
  }

  render(p, st) {
    const [LoadingI, TI] = this.c2(Loading, T);
    return <div>
      <RestErrCo e={st.e} />
      {!st.e && !st.orderInfo && <LoadingI/>}
      {!st.e && !!st.orderInfo && <div>
        <div class={bulma.buttons}>
          <button class={bulma.button} onClick={this.cannotExecOrder}>
            <TI m="order issue"/>
          </button>
          <button class={bulma.button} onClick={this.orderPickedUpByCustomer}>
            <TI m="order picked up"/>
          </button>
        </div>
        { st.showIssueTypes && <div class={bulma.buttons}>
          <button class={bulma.button} onClick={this.customerLate}>
            <TI m="customer late"/>
          </button>
          <button class={bulma.button} onClick={this.lowFood}>
            <TI m="low food"/>
          </button>
          <button class={bulma.button} onClick={this.kelnerNeedRest}>
            <TI m="need rest"/>
          </button>
        </div>}
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
