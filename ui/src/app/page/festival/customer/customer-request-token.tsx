import { h } from 'preact';

import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import {Fid} from 'app/page/festival/festival-types';

import bulma from 'app/style/my-bulma.sass';
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import {TokenSr} from "app/service/token-service";
import {RechargeTokenForm} from "./recharge-token-form";

export interface CustomerRequestTokenP {
  fid: Fid;
}

export interface CustomerRequestTokenS extends TransComS {}

class CustomerRequestToken extends TransCom<CustomerRequestTokenP, CustomerRequestTokenS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, RechargeTokenFormI]
      = this.c3(T, TitleStdMainMenu, RechargeTokenForm);
    return <div>
      <TitleStdMainMenuI t$title="Token request"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/orders/${p.fid}`}
                                           t$label="my orders" />,
                           <NavbarLinkItem path={`/festival/visitor/menu/${p.fid}`}
                                           t$label="meal menu" />
                         ]}/>
      <SecCon css={bulma.content}>
        <RechargeTokenFormI fid={p.fid} minQuote={1} />
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerRequestToken> {
  return regBundleCtx(bundleName, mainContainer, CustomerRequestToken,
    o => o.bind([
      ['tokenSr', TokenSr]
    ]) as FwdContainer);
}
