import { h } from 'preact';
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
    const [TitleStdMainMenuI, LoadingI, KelnerOrderI] = this.c3(TitleStdMainMenu, Loading, KelnerOrder);

    return <div>
      <TitleStdMainMenuI t$title="Kelner service"/>
      <SecCon css={bulma.content}>
        <RestErrCo e={st.e} />
        {!st.festState && !st.e && <LoadingI/>}
        {st.festState == Announce && <div>
          Festival is not started yet.
        </div>}
        {st.festState == Close && <div>
          Festival is over. Thanks for your help.
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
