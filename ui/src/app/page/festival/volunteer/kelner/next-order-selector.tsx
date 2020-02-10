import { h } from 'preact';

import { route } from 'preact-router';
import {reloadPage} from "util/routing";
import {TransCom, TransComS} from "i18n/trans-component";
import {RestErrCo} from "component/err/error";
import {Loading} from "component/loading";
import {U} from "util/const";
import {OrderSr} from "app/service/order-service";
import {OrderLabel} from "app/types/order";
import {Fid} from "app/page/festival/festival-types";
import { T } from 'i18n/translate-tag';
import { jne } from 'collection/join-non-empty';

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
      {!st.e && st.readyToTakeOrders === 0 && <div class={jne(bulma.message, bulma.isInfo)}>
        <div class={bulma.messageHeader}>
          <TI m="Line is empty"/>
        </div>
        <div class={bulma.messageBody}>
          <button class={jne(bulma.button, bulma.isPrimary)}
                  onClick={reloadPage}>
            <TI m="reload page" />
         </button>
        </div>
      </div>}
      {!st.e && !!st.readyToTakeOrders && <div class={jne(bulma.message, bulma.isSuccess)}>
        <div class={bulma.messageHeader}>
          <TI m="there is an order ready to be executed" an={st.readyToTakeOrders} />
        </div>
        <div class={bulma.messageBody}>
          <button class={jne(bulma.button, bulma.isPrimary)}
                  onClick={this.tryTakeOrder}>
            <TI m="try take order" />
          </button>
        </div>
      </div>}
    </div>;
  }

  at() { return []; }
}
