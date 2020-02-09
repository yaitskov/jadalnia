import { h } from 'preact';
import {route} from 'preact-router';

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
import {resolved, Thenable} from "async/abortable-promise";
import { FestMenuSr } from 'app/service/fest-menu-service';
import {MenuItemView} from "app/service/fest-menu-types";
import { U } from 'util/const';
import { jne } from 'collection/join-non-empty';
import {OrderSr} from "app/service/order-service";
import { OrderItem, orderItems } from 'app/types/order';
import {TokenSr} from "app/service/token-service";
import { CustomerOrderBuilder } from "app/page/festival/customer/customer-order-builder";
import bulma from 'app/style/my-bulma.sass';
import { NavbarLinkItem } from 'app/component/navbar-link-item';


export interface CustomerMenuP {
  fid: Fid;
}

export interface CustomerMenuS extends TransComS {
  menu?: MenuItemView[];
  mealSelections?: number[];
  currentBalance?: number;
  e?: Error;
}

class CustomerMenu extends TransCom<CustomerMenuP, CustomerMenuS> {
  // @ts-ignore
  private $festMenuSr: MenuService;
  // @ts-ignore
  private $tokenSr: TokenSr;
  // @ts-ignore
  private $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
    this.putOrder = this.putOrder.bind(this);
    this.mealSelected = this.mealSelected.bind(this);
  }

  putOrder(): Thenable<any> {
    let items: OrderItem[] = orderItems(this.st.menu!!, this.st.mealSelections!!);

    return this.$orderSr.customerPutOrder(items).tn(orderLabel =>
      route(`/festival/visitor/order/autopay/${this.pr.fid}/${orderLabel}`))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  wMnt() {
    this.$festMenuSr.list(this.pr.fid).tn(
      lst => this.ust(st => ({...st, mealSelections: new Int32Array(lst.length), menu: lst})))
      .ctch(e => this.ust(st => ({...st, e: e})));
    this.$tokenSr.getBalance().tn(balanceView => balanceView.effectiveTokens)
      .tn(balance => this.ust(st => ({...st, currentBalance: balance})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  mealSelected(idx: number, quantity: number): Thenable<any> {
    let newSelections = [...this.st.mealSelections!!];
    newSelections[idx] = quantity;
    this.ust(st => ({...st, mealSelections: newSelections}));
    return resolved(1);
  }

  render(p, st) {
    const [TitleStdMainMenuI, LoadingI, CustomerOrderBuilderI]
      = this.c3(TitleStdMainMenu, Loading, CustomerOrderBuilder);
    return <div>
      <TitleStdMainMenuI t$title="Choose meals"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/orders/${p.fid}`}
                                           t$label="my orders" />
                         ]}/>
      {(!st.menu || st.currentBalance === U) && !st.e && <LoadingI/>}
      <RestErrCo e={st.e} />
      {!!st.menu && st.currentBalance !== U && <CustomerOrderBuilderI
                      menu={st.menu}
                      mealSelections={st.mealSelections}
                      newMeals={[]}
                      missingMeals={[]}
                      validationError={<span/>}
                      currentBalance={st.currentBalance}
                      onPutOrder={this.putOrder}
                      onMealSelected={this.mealSelected} />}
     </div>;
  }

  at(): string[] { return []; }
}


export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerMenu> {
  return regBundleCtx(bundleName, mainContainer, CustomerMenu,
      o => o.bind([
        ['festMenuSr', FestMenuSr],
        ['orderSr', OrderSr],
        ['tokenSr', TokenSr],
      ]) as FwdContainer);
}
