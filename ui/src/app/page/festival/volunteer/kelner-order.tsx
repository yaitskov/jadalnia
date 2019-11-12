import { h } from 'preact';
import {TransCom, TransComS} from "i18n/trans-component";
import {Fid} from "app/page/festival/festival-types";
import {RestErrCo} from "component/err/error";
import {Loading} from "component/loading";
import {U} from "util/const";
import {OrderSr} from "app/service/order-service";
import {OrderLabel} from "app/types/order";
import {KelnerTakenOrder} from "app/page/festival/volunteer/kelner/kelner-taken-order";
import {KelnerNextOrderSelector} from "app/page/festival/volunteer/kelner/next-order-selector";
import { T } from 'i18n/translate-tag';

export interface KelnerOrderP {
  fid: Fid;
}

export interface KelnerOrderS extends TransComS {
  takenOrderId?: OrderLabel;
  e?: Error;
}

export class KelnerOrder extends TransCom<KelnerOrderP, KelnerOrderS> {
  // @ts-ignore
  $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()}
  }

  wMnt() {
    this.$orderSr.kelnerTakenOrderId()
      .tn(mayBeOrderId => this.ust(st => ({...st, e: U, takenOrderId: mayBeOrderId})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [LoadingI, KelnerNextOrderSelectorI, KelnerTakenOrderI, TI]
      = this.c4(Loading, KelnerNextOrderSelector, KelnerTakenOrder, T);
    return <div>
      <RestErrCo e={st.e} />
      {!st.e && st.takenOrderId === U && <LoadingI/>}
      {!st.e && st.takenOrderId === null && <div>
        <p><TI m="no order on you"/></p>
        <KelnerNextOrderSelectorI fid={p.fid} />
      </div>}
      {!st.e && !!st.takenOrderId && <KelnerTakenOrderI fid={p.fid} orderLbl={st.takenOrderId}/>}
    </div>;
  }

  at() { return []; }
}
