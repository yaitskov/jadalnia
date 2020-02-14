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
import {ReturnTokenForm} from "app/page/festival/customer/return-token-form";

export interface CustomerRequestTokenReturnP {
  fid: Fid;
  maxQuote: number;
}

export interface CustomerRequestTokenReturnS extends TransComS {}

class CustomerRequestTokenReturn extends TransCom<CustomerRequestTokenReturnP, CustomerRequestTokenReturnS> {
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
    const [TI, TitleStdMainMenuI, ReturnTokenFormI]
      = this.c3(T, TitleStdMainMenu, ReturnTokenForm);
    return <div>
      <TitleStdMainMenuI t$title="Token request"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/orders/${p.fid}`}
                                           t$label="my orders" />,
                           <NavbarLinkItem path={`/festival/visitor/menu/${p.fid}`}
                                           t$label="meal menu" />
                         ]}/>
      <SecCon css={bulma.content}>
        <ReturnTokenFormI fid={p.fid} maxQuote={p.maxQuote} />
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerRequestTokenReturn> {
  return regBundleCtx(bundleName, mainContainer, CustomerRequestTokenReturn,
    o => o.bind([
      ['tokenSr', TokenSr]
    ]) as FwdContainer);
}
