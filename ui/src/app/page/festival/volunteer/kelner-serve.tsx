import { h } from 'preact';

import { jne } from 'collection/join-non-empty';
import {reloadPage} from "util/routing";
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Announce, Close, FestState, Fid, Open} from 'app/page/festival/festival-types';
import {FestSr} from "app/service/fest-service";
import {RestErrCo} from "component/err/error";
import {TransCom, TransComS} from "i18n/trans-component";
import {SecCon} from "app/component/section-container";
import {TitleStdMainMenu} from "app/title-std-main-menu";
import bulma from 'app/style/my-bulma.sass';
import {Loading} from "component/loading";
import {OrderSr} from "app/service/order-service";
import {KelnerOrder} from "app/page/festival/volunteer/kelner-order";
import { T } from 'i18n/translate-tag';

export interface KelnerServeS extends TransComS {
  festState?: FestState;
  e?: Error;
}

class KelnerServe extends TransCom<{fid: Fid}, KelnerServeS> {
  // @ts-ignore
  $festSr: FestSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$festSr.getState(this.pr.fid)
      .tn(festSt => this.ust(st => ({...st, festState: festSt})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [TitleStdMainMenuI, LoadingI, KelnerOrderI, TI]
      = this.c4(TitleStdMainMenu, Loading, KelnerOrder, T);

    return <div>
      <TitleStdMainMenuI t$title="Kelner service"/>
      <SecCon css={bulma.content}>
        <RestErrCo e={st.e} />
        {!st.festState && !st.e && <LoadingI/>}
        {st.festState == Announce && <div class={jne(bulma.message, bulma.isInfo)}>
          <div class={bulma.messageHeader}>
            <TI m="Festival is not started yet"/>
          </div>
          <div class={bulma.messageBody}>
            <button class={jne(bulma.button, bulma.isPrimary)}
                    onClick={reloadPage}>
              <TI m="reload page" />
            </button>
          </div>
        </div>}
        {st.festState == Close && <div class={jne(bulma.message, bulma.isInfo)}>
          <div class={bulma.messageHeader}>
            <TI m="Festival is over"/>
          </div>
          <div class={bulma.messageBody}>
            <button class={jne(bulma.button, bulma.isPrimary)}
                    onClick={reloadPage}>
              <TI m="reload page" />
           </button>
          </div>
        </div>}
        {st.festState == Open && <div>
          <KelnerOrderI fid={p.fid} />
        </div>}
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}


export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KelnerServe> {
  return regBundleCtx(bundleName, mainContainer, KelnerServe,
      o => o.bind([['festSr', FestSr], ['orderSr', OrderSr]]) as FwdContainer);
}
