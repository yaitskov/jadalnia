import { h } from 'preact';
import {Link} from 'preact-router';

import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";

import bulma from 'app/style/my-bulma.sass';
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import {TokenRequestId, TokenRequestVisitorView, TokenSr} from "app/service/token-service";
import {Loading} from "component/loading";
import { jne } from 'collection/join-non-empty';
import {reloadPage} from "util/routing";
import {UserAuth} from "app/auth/user-auth";
import {OrderLabel} from "app/types/order";

export interface TokenRequestP {
  fid: Fid;
  order?: OrderLabel;
  tokReq: TokenRequestId;
}

export interface TokenRequestS extends TransComS {
  e?: Error;
  tokenRequestInfo?: TokenRequestVisitorView;
}

class TokenRequest extends TransCom<TokenRequestP, TokenRequestS> {
  // @ts-ignore
  private $tokenSr: TokenSr;
  // @ts-ignore
  private $userAuth: UserAuth;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$tokenSr.showRequestTokenToVisitor(this.pr.tokReq)
      .tn(tokReqInfo => this.ust(st => ({...st, tokenRequestInfo: tokReqInfo})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI] =
      this.c3(T, TitleStdMainMenu, Loading);

    return <div>
      <TitleStdMainMenuI t$title="Token request"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/balance/${p.fid}`}
                                           t$label="My balance" />,
                           <NavbarLinkItem path={`/festival/visitor/orders/${p.fid}`}
                                           t$label="my orders" />,
                           <NavbarLinkItem path={`/festival/visitor/menu/${p.fid}`}
                                           t$label="meal menu" />,
                         ]}/>
      <SecCon css={bulma.content}>
        <p>
          { !!st.tokenRequestInfo && <TI m="x tokens is requested." x={st.tokenRequestInfo.amount}/> }
        </p>
        <p>
          <TI m="Your visitor id" id={this.$userAuth.myUid()}/>
        </p>
          { !!st.tokenRequestInfo && st.tokenRequestInfo.approved && <p>
           <TI m="Token request is approved."/>
        </p>}
        { !!st.tokenRequestInfo && !st.tokenRequestInfo.approved && <p>
          <TI m="Token request is not approved."/>
          <TI m="Contact cashier pay for amount of requested tokens."/>
        </p>}
        <div class={bulma.buttons}>
          {!!st.tokenRequestInfo && !st.tokenRequestInfo.approved && <button class={jne(bulma.button, bulma.isPrimary)}
                  onClick={reloadPage}>
            <TI m="check token request that approved" />
          </button>}
          {!!st.tokenRequestInfo
            && st.tokenRequestInfo.approved
            && (p.order != '0')
            && <Link href={`/festival/visitor/order/autopay/${p.fid}/${p.order}`}
                     class={jne(bulma.button, bulma.isPrimary)}>
                 <TI m="Pay order" o={p.order} />
               </Link>}
          {!!st.tokenRequestInfo
            && st.tokenRequestInfo.approved
            && (p.order == '0')
            && <Link href={`/festival/visitor/orders/${p.fid}`}
                     class={jne(bulma.button, bulma.isPrimary)}>
                 <TI m="To my order" />
               </Link>}
        </div>
        { !st.e && !st.tokenRequestInfo && <LoadingI /> }
        <RestErrCo e={st.e} />
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<TokenRequest> {
  return regBundleCtx(bundleName, mainContainer, TokenRequest,
    o => o.bind([
      ['tokenSr', TokenSr],
    ]) as FwdContainer);
}
