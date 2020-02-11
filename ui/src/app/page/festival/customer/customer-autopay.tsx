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
import {Loading} from "component/loading";
import { U } from 'util/const';
import {OrderSr} from "app/service/order-service";
import {OrderInfoCustomerView, OrderLabel, OrderPayResult} from 'app/types/order';
import {TokenBalanceView, TokenSr} from "app/service/token-service";

import bulma from 'app/style/my-bulma.sass';
import {OrderProgressView} from "app/page/festival/customer/order-progress-view";
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import {RechargeTokenForm} from "app/page/festival/customer/recharge-token-form";

export interface CustomerAutopayP {
  fid: Fid;
  order: OrderLabel;
}

export interface CustomerAutopayS extends TransComS {
  e?: Error;
  orderInfo?: OrderInfoCustomerView;
  payResult?: OrderPayResult;
  balance?: TokenBalanceView;
}

class CustomerAutopay extends TransCom<CustomerAutopayP, CustomerAutopayS> {
  // @ts-ignore
  private $tokenSr: TokenSr;
  // @ts-ignore
  private $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.tryToPay();
    this.$orderSr.getInfoForVisitor(this.pr.order)
      .tn(orderInfo => this.ust(st => ({...st, orderInfo: orderInfo})));
    this.$tokenSr.getBalance().tn(balanceView => balanceView.effectiveTokens)
      .tn(balance => this.ust(st => ({...st, balance: balance})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  tryToPay() {
    this.$orderSr.customerPaysOrder(this.pr.order)
      .tn(payResult => this.ust(st => ({...st, payResult: payResult})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI] = this.c2(T, TitleStdMainMenu);
    const [LoadingI, OrderProgressViewI, RechargeTokenFormI] =
      this.c3(Loading, OrderProgressView, RechargeTokenForm);

    return <div>
      <TitleStdMainMenuI t$title="Order payment"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/order/control/${p.fid}/${p.order}`}
                                           t$label="order page" />,
                           <NavbarLinkItem path={`/festival/visitor/orders/${p.fid}`}
                                           t$label="my orders" />,
                           <NavbarLinkItem path={`/festival/visitor/menu/${p.fid}`}
                                           t$label="meal menu" />,
                         ]}/>
      <SecCon css={bulma.content}>
        <p>
          <TI m="Your order number is o" o={p.order}/>
        </p>
        {st.payResult === U && <LoadingI />}
        {st.payResult === 'ORDER_PAID' && <div>
          <p>
            <TI m="Order was just paid."/>
          </p>
          <OrderProgressViewI fid={p.fid} ordLbl={p.order} />
        </div>}
        {st.payResult === 'ALREADY_PAID' && <div>
          <p>
            <TI m="Order is already paid"/>
          </p>
          <OrderProgressViewI fid={p.fid} ordLbl={p.order} />
        </div>}
        {st.payResult === 'FESTIVAL_OVER' && <div>
          <TI m="Order is not paid due festival is over."/>
        </div>}
        {st.payResult === 'RETRY' && <div>
          <p>
            <TI m="order is not paid due technical reason"/>
            <TI m="Retry later."/>
          </p>
          <OrderProgressViewI fid={p.fid} ordLbl={p.order} />
        </div>}
        {st.payResult === 'CANCELLED' && <div>
          <p>
            <TI m="Order is not paid because it has been cancelled."/>
            <TI m="Put similar order again if you want." />
          </p>
          <OrderProgressViewI fid={p.fid} ordLbl={p.order} />
        </div>}
        {st.payResult === 'NOT_ENOUGH_FUNDS' && <div>
          <p>
            <TI m="Not enough tokens to pay order."/>
          </p>
          { !!st.orderInfo && <RechargeTokenFormI fid={p.fid}
                                                  order={p.order}
                                                  minQuote={st.orderInfo.price - st.balance} /> }
        </div>}
        <RestErrCo e={st.e} />
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerAutopay> {
  return regBundleCtx(bundleName, mainContainer, CustomerAutopay,
      o => o.bind([
        ['orderSr', OrderSr],
        ['tokenSr', TokenSr],
      ]) as FwdContainer);
}
