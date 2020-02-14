import { h } from 'preact';
import {Link} from 'preact-router';

import { jne } from 'collection/join-non-empty';
import {reloadPage} from "util/routing";
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";
import {Loading} from "component/loading";

import bulma from 'app/style/my-bulma.sass';
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import {TokenSr, TokenBalanceView} from "app/service/token-service";

export interface CustomerBalanceP {
  fid: Fid;
}

export interface CustomerBalanceS extends TransComS {
  balance?: TokenBalanceView;
  e?: Error;
}

class CustomerBalance extends TransCom<CustomerBalanceP, CustomerBalanceS> {
  // @ts-ignore
  private $userAuth: UserAuth;

  // @ts-ignore
  private $tokenSr: TokenSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$tokenSr.getBalance()
      .tn(balance => this.ust(st => ({...st, balance})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI] = this.c3(T, TitleStdMainMenu, Loading);
    return <div>
      <TitleStdMainMenuI t$title="My Balance"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/orders/${p.fid}`}
                                           t$label="my orders" />,
                           <NavbarLinkItem path={`/festival/visitor/menu/${p.fid}`}
                                           t$label="meal menu" />
                         ]}/>
      <SecCon css={bulma.content}>
        {!st.balance && !st.e && <LoadingI/>}
        <RestErrCo e={st.e} />
        {!!st.balance && <table class={bulma.table}>
          <tr>
            <td>
              <TI m="Your ID"/>
            </td>
            <td>{this.$userAuth.myUid()}</td>
          </tr>
          <tr>
            <td>
              <TI m="Pending for approve"/>
            </td>
            <td>{st.balance.pendingTokens - st.balance.effectiveTokens}</td>
          </tr>
          <tr>
            <td>
              <TI m="Available tokens" />
            </td>
            <td>
              {st.balance.effectiveTokens}
            </td>
          </tr>
        </table>}
        <div class={bulma.buttons}>
          <Link
            class={jne(bulma.button, bulma.isSuccess)}
            href={`/festival/visitor/request-tokens/${p.fid}`}>
            <TI m="refill balance" />
          </Link>
          {!!st.balance && st.balance.effectiveTokens > 0 && <Link
            class={jne(bulma.button, bulma.isWarning)}
            href={`/festival/visitor/request-token-return/${p.fid}/${st.balance.effectiveTokens}`}>
            <TI m="return cash" />
          </Link>}
          <button class={jne(bulma.button, bulma.isPrimary)}
                  onClick={reloadPage}>
            <TI m="reload page" />
          </button>
        </div>
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerBalance> {
  return regBundleCtx(bundleName, mainContainer, CustomerBalance,
      o => o.bind([
        ['tokenSr', TokenSr]
      ]) as FwdContainer);
}
