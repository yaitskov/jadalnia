import { h } from 'preact';

import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";
import {OrderSr} from "app/service/order-service";
import {OrderInfoCustomerView, OrderLabel} from 'app/types/order';
import {OrderProgressView} from "app/page/festival/customer/order-progress-view";

import bulma from 'app/style/my-bulma.sass';
import { NavbarLinkItem } from 'app/component/navbar-link-item';

export interface CustomerOrderControlP {
  fid: Fid;
  order: OrderLabel;
}

export interface CustomerOrderControlS extends TransComS {
  e?: Error;
  orderInfo?: OrderInfoCustomerView;
}

class CustomerOrderControl extends TransCom<CustomerOrderControlP, CustomerOrderControlS> {
  // @ts-ignore
  private $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$orderSr.getInfoForVisitor(this.pr.order)
      .tn(orderInfo => this.ust(st => ({...st, orderInfo: orderInfo})));
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, OrderProgressViewI] =
      this.c3(T, TitleStdMainMenu, OrderProgressView);

    return <div>
      <TitleStdMainMenuI t$title="Order control"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/balance/${p.fid}`}
                                           t$label="balance" />,
                           <NavbarLinkItem path={`/festival/visitor/orders/${p.fid}`}
                                           t$label="my orders" />,
                           <NavbarLinkItem path={`/festival/visitor/menu/${p.fid}`}
                                           t$label="meal menu" />,
                         ]}/>
      <SecCon css={bulma.content}>
        <p>
          <TI m="Your order number is o" o={p.order}/>
        </p>
        <OrderProgressViewI fid={p.fid} ordLbl={p.order} />
        <RestErrCo e={st.e} />
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerOrderControl> {
  return regBundleCtx(bundleName, mainContainer, CustomerOrderControl,
    o => o.bind([
      ['orderSr', OrderSr],
    ]) as FwdContainer);
}
