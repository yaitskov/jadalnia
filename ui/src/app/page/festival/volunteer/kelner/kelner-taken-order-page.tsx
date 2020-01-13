import { h } from 'preact';
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Fid} from 'app/page/festival/festival-types';
import {TransCom, TransComS} from "i18n/trans-component";
import {SecCon} from "app/component/section-container";
import {TitleStdMainMenu2} from "app/title-std-main-menu-2";

import {OrderSr} from "app/service/order-service";
import {KelnerTakenOrder} from "app/page/festival/volunteer/kelner/kelner-taken-order";
import { OrderLabel } from "app/types/order";

import bulma from 'app/style/my-bulma.sass';
import {T} from "i18n/translate-tag";

export interface KelnerTakenOrderPageP {
  fid: Fid;
  order: OrderLabel;
}

export interface KelnerTakenOrderPageS extends TransComS {
  e?: Error;
}

class KelnerTakenOrderPage extends TransCom<KelnerTakenOrderPageP, KelnerTakenOrderPageS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p) {
    const [TitleStdMainMenuI, KelnerTakenOrderI, TI]
      = this.c3(TitleStdMainMenu2, KelnerTakenOrder, T);

    return <div>
      <TitleStdMainMenuI title={<TI m="Exec order" lbl={p.order} />} />
      <SecCon css={bulma.content}>
        <KelnerTakenOrderI fid={p.fid} orderLbl={p.order}/>
      </SecCon>
    </div>;
  }

  at() { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KelnerTakenOrderPage> {
  return regBundleCtx(bundleName, mainContainer, KelnerTakenOrderPage,
      o => o.bind([['orderSr', OrderSr]]) as FwdContainer);
}
