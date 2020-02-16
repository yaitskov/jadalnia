import { h } from 'preact';

import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable, deepEq } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";

import bulma from 'app/style/my-bulma.sass';
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import {KasierTokenView, TokenCancelOutcome, TokenRequestId, TokenSr} from "app/service/token-service";
import { Loading } from 'component/loading';
import {BackBtn} from "component/form/back-button";
import { jne } from 'collection/join-non-empty';
import { Link } from 'preact-router';

export interface KasierRequestControlP {
  fid: Fid;
  tok: TokenRequestId;
}

export interface KasierRequestControlS extends TransComS {
  e?: Error;
  tokenInfo?: KasierTokenView;
  outcome?: TokenCancelOutcome;
}

class KasierRequestControl extends TransCom<KasierRequestControlP, KasierRequestControlS> {
  // @ts-ignore
  private $tokenSr: TokenSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
    this.cancelRequest = this.cancelRequest.bind(this);
  }

  cancelRequest() {
    this.$tokenSr.cancelToken(this.pr.tok)
      .tn(outcome => this.ust(st => ({...st, outcome})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  onUp(prevProps: any) {
    if (!deepEq(this.pr as any, prevProps)) {
      this.wMnt()
    }
  }

  wMnt() {
    this.$tokenSr.showRequestTokenToKasier(this.pr.tok)
      .tn(tokenInfo => this.ust(st => ({...st, tokenInfo})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st: KasierRequestControlS) {
    const [TI, LoadingI, TitleStdMainMenuI] = this.c3(T, Loading, TitleStdMainMenu);

    return <div>
      <TitleStdMainMenuI t$title="Token control"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/kasier/history/${p.fid}/0`}
                                           t$label="History" />
                         ]}/>
      <section class={bulma.section}>
        {!st.tokenInfo && !st.e && <LoadingI /> }
        <RestErrCo e={st.e} />
        {!!st.tokenInfo && <div class={bulma.content}>
           <table class={bulma.table}>
            <tr>
              <td>
                <TI m="Request Id" />
              </td>
              <td>
                {st.tokenInfo.tokenId}
              </td>
            </tr>
            <tr>
              <td>
                <TI m="Approved at" />
              </td>
              <td>
                {st.tokenInfo.approvedAt}
              </td>
            </tr>
            <tr>
              <td>
                <TI m="Customer" />
              </td>
              <td>
                {st.tokenInfo.customer}
              </td>
            </tr>
            <tr>
              <td><TI m="Operation"/></td>
              <td>
                {st.tokenInfo.amount < 0 ? <TI m="Return"/> : <TI m="Purchase" />}
              </td>
            </tr>
            <tr>
              <td><TI m="Tokens"/></td>
              <td>{st.tokenInfo.amount}</td>
            </tr>
             {!!st.tokenInfo.cancelledBy && <tr>
              <td colSpan={2}>
                <Link href={`/festival/kasier/request/control/${p.fid}/${st.tokenInfo.cancelledBy}`}>
                  <TI m="Cancelled" />
                </Link>
              </td>
            </tr>}
          </table>

          {!!st.outcome && <div class={jne(bulma.message, bulma.isInfo)}>
            <div class={bulma.messageHeader}>
              <TI m="Cancellation outcome"/>
            </div>
            {st.outcome == 'CANCELLED' && <div class={bulma.messageBody}>
              <TI m="Token has been cancelled. " />
            </div>}
            {st.outcome == 'NOT_ENOUGH_FUNDS' && <div class={bulma.messageBody}>
              <TI m="Token has not been cancelled, due customer balance is too low. " />
            </div>}
          </div>}

          <div class={bulma.buttons} >
            <BackBtn />
            {!st.tokenInfo.cancelledBy && !st.outcome && <button
              class={jne(bulma.button, bulma.isDanger)}
              onClick={this.cancelRequest}>
              <TI m="Cancel" />
            </button>}
          </div>
        </div>}
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KasierRequestControl> {
  return regBundleCtx(bundleName, mainContainer, KasierRequestControl,
    o => o.bind([
      ['tokenSr', TokenSr],
    ]) as FwdContainer);
}
