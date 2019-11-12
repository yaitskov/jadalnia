import { h } from 'preact';
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";
import {TransCom, TransComS} from "i18n/trans-component";
import {SecCon} from "app/component/section-container";
import {TitleStdMainMenu} from "app/title-std-main-menu";

import {OrderSr} from "app/service/order-service";
import {KelnerTakenOrder} from "app/page/festival/volunteer/kelner/kelner-taken-order";
import { OrderLabel } from "app/types/order";

import bulma from 'app/style/my-bulma.sass';

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
    const [TitleStdMainMenuI, KelnerTakenOrderI]
      = this.c2(TitleStdMainMenu, KelnerTakenOrder);

    return <div>
      <TitleStdMainMenuI t$title="Exec order"/>
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
