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
import {Loading} from "component/loading";
import {OrderSr} from "app/service/order-service";
import {OrderInfoCustomerView} from 'app/types/order';

import bulma from 'app/style/my-bulma.sass';
import { NavbarLinkItem } from 'app/component/navbar-link-item';

export interface CustomerOrdersP {
  fid: Fid;
}

export interface CustomerOrdersS extends TransComS {
  orders?: OrderInfoCustomerView[];
  e?: Error;
}

class CustomerOrders extends TransCom<CustomerOrdersP, CustomerOrdersS> {
  // @ts-ignore
  private $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$orderSr.customerLists()
      .tn(lst => this.ust(st => ({...st, orders: lst})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI] = this.c3(T, TitleStdMainMenu, Loading);
    return <div>
      <TitleStdMainMenuI t$title="My orders"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/menu/${p.fid}`}
                                           t$label="meal menu" />
                         ]}/>
      <SecCon css={bulma.content}>
        {!st.orders && !st.e && <LoadingI/>}
        <RestErrCo e={st.e} />
        {!!st.orders && <ul class={bulma.list}>
          {!!st.orders.length || <li>No orders</li>}
          {st.orders.map((order: OrderInfoCustomerView, i: number) => <li>
            <Link href={`/festival/visitor/order/control/${p.fid}/${order.label}`}
                  class={bulma.button}>
              {order.label} / {order.state} / {order.price}
            </Link>
          </li>)}
        </ul>}
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerOrders> {
  return regBundleCtx(bundleName, mainContainer, CustomerOrders,
      o => o.bind([
        ['orderSr', OrderSr]
      ]) as FwdContainer);
}
