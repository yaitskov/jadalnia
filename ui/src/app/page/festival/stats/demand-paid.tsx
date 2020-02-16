import { h } from 'preact';

import { U } from 'util/const';
import {Loading} from "component/loading";
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";
import { DishName  } from "app/types/menu";
import bulma from 'app/style/my-bulma.sass';
import {Dish2Quantity, OrderStatsSr} from 'app/service/order-stats-service';
import {jne} from "collection/join-non-empty";

export interface DemandPaidP {
  fid: Fid;
}

export interface DishDemand {
  name: DishName;
  quantity: number;
}

export interface DemandPaidS extends TransComS {
  e?: Error;
  stats?: DishDemand[];
}

class DemandPaid  extends TransCom<DemandPaidP, DemandPaidS> {
  // @ts-ignore
  private $orderStatsSr: OrderStatsSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$orderStatsSr.demandPaid(this.pr.fid)
      .tn(r => this.ust(
        st => ({...st, stats: this.mapToSortedList(r.meals)})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  mapToSortedList(m: Dish2Quantity): DishDemand[] {
    return Object.keys(m)
      .map(dish => ({name: dish, quantity: m[dish]}))
      .sort((a, b) => b.quantity - a.quantity);
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI]
      = this.c3(T, TitleStdMainMenu, Loading);

    return <div>
      <TitleStdMainMenuI t$title="Demand of paid orders" />
      <section class={jne(bulma.section, bulma.content)}>
        {st.stats === U  && !st.e && <LoadingI/>}
        <RestErrCo e={st.e} />
        {!!st.stats && !st.stats.length && <div class={jne(bulma.message, bulma.isInfo)}>
          <div class={bulma.messageHeader}>
            <TI m="No paid orders"/>
          </div>
        </div>}
        {!!st.stats && !!st.stats.length && <table class={bulma.table}>
          <tr>
            <td><TI m="Dish name"/></td>
            <td><TI m="Quantity"/></td>
          </tr>
          {st.stats.map((d2q: DishDemand) => <tr>
            <td>{d2q.name}</td>
            <td>{d2q.quantity}</td>
          </tr>)}
        </table>}
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<DemandPaid> {
  return regBundleCtx(bundleName, mainContainer, DemandPaid,
    o => o.bind([
      ['orderStatsSr', OrderStatsSr],
    ]) as FwdContainer);
}
