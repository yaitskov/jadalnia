import { h } from 'preact';
import { route } from 'preact-router';
import {TransCom, TransComS} from "i18n/trans-component";
import {RestErrCo} from "component/err/error";
import {Loading} from "component/loading";
import {U} from "util/const";
import {OrderSr} from "app/service/order-service";
import {OrderLabel} from "app/types/order";
import {Fid} from "app/page/festival/festival-types";
import { T } from 'i18n/translate-tag';

import bulma from "app/style/my-bulma.sass";

export interface KelnerNextOrderSelectorP {
  fid: Fid;
}

export interface KelnerNextOrderSelectorS extends TransComS {
  readyToTakeOrders?: number;
  e?: Error;
}

export class KelnerNextOrderSelector extends TransCom<KelnerNextOrderSelectorP, KelnerNextOrderSelectorS> {
  // @ts-ignore
  $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
    this.tryTakeOrder = this.tryTakeOrder.bind(this);
  }

  wMnt() {
    this.$orderSr.countOrdersReadyForExec()
      .tn(numOfOrders => this.ust(st => ({...st, readyToTakeOrders: numOfOrders})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  tryTakeOrder() {
    this.$orderSr.takeOrderForExec().tn(mayBeOrd => {
      if (mayBeOrd) {
        route(`/festival/kelner/serve/order/${this.pr.fid}/${mayBeOrd}`)
      } else {
        this.ust(st => ({...st, e: null, readyToTakeOrders: 0}));
      }
    }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [LoadingI, TI] = this.c2(Loading, T);
    return <div>
      <RestErrCo e={st.e} />
      {!st.e && st.readyToTakeOrders === U && <LoadingI/>}
      {!st.e && st.readyToTakeOrders === 0 && <TI m="Line is empty"/>}
      {!st.e && !!st.readyToTakeOrders && <div>
        <p>
          <TI m="there is an order ready to be executed" an={st.readyToTakeOrders} />
        </p>
        <div class={bulma.buttons}>
          <button class={bulma.button} onClick={this.tryTakeOrder}>
            <TI m="try take order" />
          </button>
        </div>
      </div>}
    </div>;
  }

  at() { return []; }
}
