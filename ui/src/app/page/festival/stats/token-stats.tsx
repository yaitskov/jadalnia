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

import bulma from 'app/style/my-bulma.sass';

import {jne} from "collection/join-non-empty";
import {TokenStatsSr} from 'app/service/token-stats-service';
import { TokenStatsResponse } from 'app/types/token';

export interface TokenStatsP {
  fid: Fid;
}

export interface TokenStatsS extends TransComS {
  e?: Error;
  stats?: TokenStatsResponse;
}

class TokenStats  extends TransCom<TokenStatsP, TokenStatsS> {
  // @ts-ignore
  private $tokenStatsSr: TokenStatsSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$tokenStatsSr.tokenStats(this.pr.fid)
      .tn(stats => this.ust(st => ({...st, stats: stats})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI]
      = this.c3(T, TitleStdMainMenu, Loading);

    return <div>
      <TitleStdMainMenuI t$title="Token stats" />
      <section class={jne(bulma.section, bulma.content)}>
        {st.stats === U  && !st.e && <LoadingI/>}
        <RestErrCo e={st.e} />
        {!!st.stats && <table class={bulma.table}>
          <tr>
            <td><TI m="Tokens sold to customers"/></td><td>{st.stats.boughtByCustomers}</td>
          </tr>
          <tr>
            <td><TI m="Tokens returned by customers"/></td><td>{st.stats.returnedToCustomers}</td>
          </tr>
          <tr>
            <td><TI m="Tokens pending approval"/></td><td>{st.stats.pendingBoughtByCustomers}</td>
          </tr>
          <tr>
            <td><TI m="Returning tokens pending approval"/></td>
            <td>{st.stats.pendingReturnToCustomers}</td>
          </tr>
        </table>}
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<TokenStats> {
  return regBundleCtx(bundleName, mainContainer, TokenStats,
    o => o.bind([
      ['tokenStatsSr', TokenStatsSr],
    ]) as FwdContainer);
}
