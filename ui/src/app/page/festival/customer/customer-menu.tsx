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
import { OrderItem } from 'app/types/order';
import {TokenSr} from "app/service/token-service";

import bulma from 'app/style/my-bulma.sass';

export interface CustomerMenuP {
  fid: Fid;
}

export interface CustomerMenuS extends TransComS {
  menu?: MenuItemView[];
  mealSelections: number[];
  currentBalance?: number;
  e?: Error;
  puttingOrder: boolean;
}

class CustomerMenu extends TransCom<CustomerMenuP, CustomerMenuS> {
  // @ts-ignore
  private $festMenuSr: MenuService;
  // @ts-ignore
  private $customerSr: TokenSr;
  // @ts-ignore
  private $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at(), mealSelections: [], puttingOrder: false};
    this.putOrder = this.putOrder.bind(this);
  }

  putOrder(): Thenable<any> {
    let items: OrderItem[] = this.st.mealSelections.map(
      (count: number, idx: number) => ({
        name: this.st.menu![idx].name,
        quantity: count
      })).filter(item => item.quantity > 0);

    if (this.st.puttingOrder) {
      return resolved(0);
    }
    this.ust(st => ({...st, puttingOrder: true}));
    return this.$orderSr.customerPutOrder(items).tn(orderLabel =>
      route(`/festival/visitor/order/autopay/${this.pr.fid}/${orderLabel}`))
      .ctch(e => this.ust(st => ({...st, e: e, puttingOrder: false})));
  }

  wMnt() {
    this.$festMenuSr.list(this.pr.fid).tn(
      lst => this.ust(st => ({...st, mealSelections: new Int32Array(lst.length),  menu: lst})))
      .ctch(e => this.ust(st => ({...st, e: e})));
    this.$customerSr.getBalance().tn(balanceView => balanceView.effectiveTokens)
      .tn(balance => this.ust(st => ({...st, currentBalance: balance})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  toggleItem(i: number) {
    this.st.mealSelections[i] = this.st.mealSelections[i] ? 0 : 1;
  }

  addItem(i: number) {
    this.st.mealSelections[i] += this.st.mealSelections[i];
  }

  removeItem(i: number) {
    this.st.mealSelections[i] = Math.max(0, this.st.mealSelections[i] - 1);
  }

  sumMeals(): number {
    return this.st.mealSelections.reduce(
      (sum: number, count: number, idx: number) => sum + this.st.menu![idx].price * count, 0);
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI] = this.c3(T, TitleStdMainMenu, Loading);
    return <div>
      <TitleStdMainMenuI t$title="Choose meals"/>
      <SecCon css={bulma.content}>
        {!st.menu && !st.e && <LoadingI/>}
        <RestErrCo e={st.e} />
        {!!st.menu && <ul class={bulma.list}>
          {st.menu.length || <li>menu is empty</li>}
          {st.menu.map((item: MenuItemView, i: number) => <li>
            <p class={st.mealSelections[i] ? bulma.isPrimary : ''}
               onClick={() => this.toggleItem(i)}>{item.name} / {item.price}</p>
            { !!st.mealSelections[i] && <div class={bulma.buttons}>
              <button class={bulma.button} onClick={() => this.addItem(i)}>+</button>
              <button class={bulma.button}>{st.mealSelections[i]}</button>
              <button class={bulma.button} onClick={() => this.removeItem(i)}>-</button>
            </div>}
          </li>)}
        </ul>}
        <p><TI m="Total:"/></p>
        { !!st.menu && st.currentBalance !== U && <div>
          <p><TI m="Current balance" v={st.currentBalance}/></p>
          <p><TI m="Order cost" v={this.sumMeals()}/></p>
          <p><TI m="Quote to pay" v={Math.max(0, this.sumMeals() - st.currentBalance)}/></p>
          { this.sumMeals() > 0  && !st.puttingOrder && <div class={bulma.buttons} >
            <button onClick={this.putOrder}
                    class={jne(bulma.button, bulma.isPrimary, bulma.isCenter )}>
              <TI m="put order" />
            </button>
          </div>}
          { st.puttingOrder && <LoadingI t$lbl="Putting order..." />}
        </div> }
        { st.currentBalance === U && !st.e && <LoadingI /> }
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerMenu> {
  return regBundleCtx(bundleName, mainContainer, CustomerMenu,
      o => o.bind([['festMenuSr', FestMenuSr]]) as FwdContainer);
}
