import { h } from 'preact';
import { route } from 'preact-router';

import { U } from 'util/const';
import {Loading} from "component/loading";
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";
import {OrderSr} from "app/service/order-service";
import { OrderLabel, orderItems, sumMealsPrice } from 'app/types/order';
import { OrderItem, findSelectedMeals, UpdateAttemptOutcome } from 'app/types/order';

import {resolved, Thenable} from "async/abortable-promise";
import { DishName  } from "app/types/menu";
import { MenuItemView} from 'app/service/fest-menu-types';
import { CustomerOrderBuilder } from "app/page/festival/customer/customer-order-builder";
import {FestMenuSr} from "app/service/fest-menu-service";
import {TokenSr} from "app/service/token-service";

import bulma from 'app/style/my-bulma.sass';
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import {jne} from "collection/join-non-empty";

export interface CustomerOrderModificationP {
  fid: Fid;
  order: OrderLabel;
}

export interface CustomerOrderModificationS extends TransComS {
  menu?: MenuItemView[];
  originMealSelections?: number[];
  newMeals: DishName[];
  missingMeals: DishName[];
  mealSelections?: number[];
  currentBalance?: number;
  updateOutcome?:  UpdateAttemptOutcome;
  e?: Error;
}

class CustomerOrderModification
extends TransCom<CustomerOrderModificationP, CustomerOrderModificationS> {
  // @ts-ignore
  private $festMenuSr: MenuService;
  // @ts-ignore
  private $tokenSr: TokenSr;
  // @ts-ignore
  private $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at(), newMeals: [], missingMeals: []};
    this.putOrder = this.putOrder.bind(this);
    this.mealSelected = this.mealSelected.bind(this);
  }

  putOrder(): Thenable<any> {
    let items: OrderItem[] = orderItems(this.st.menu!!, this.st.mealSelections!!);

    return this.$orderSr.modifyOrder({label: this.pr.order, newItems: items})
               .tn(outcome => {
                 if (outcome == 'UPDATED') {
                   route(`/festival/visitor/order/autopay/${this.pr.fid}/${this.pr.order}`)
                 } else {
                   this.ust(st => ({...st, updateOutcome: outcome}));
                 }
               })
               .ctch(e => this.ust(st => ({...st, e: e})));
  }

  wMnt() {
    this.$festMenuSr.list(this.pr.fid)
        .tn(lst => this.ust(
          st => ({...st, menu: lst})))
        .tn(ok => this.$orderSr.getInfoForVisitor(this.pr.order).tn(orderInfo => {
          let selected = findSelectedMeals(this.st.menu!!, orderInfo.items);
          this.ust(st => ({...st,
                           mealSelections: selected,
                           originMealSelections: [...selected]}));
        }))
        .ctch(e => this.ust(st => ({...st, e: e})));
    this.$tokenSr.getBalance().tn(balanceView => balanceView.effectiveTokens)
        .tn(balance => this.ust(st => ({...st, currentBalance: balance})))
        .ctch(e => this.ust(st => ({...st, e: e})));
    this.$orderSr.listUnavailableMeals()
        .tn(missing => this.ust(st => ({...st, missingMeals: missing})))
        .ctch(e => this.ust(st => ({...st, e: e})));
  }

  mealSelected(idx: number, quantity: number): Thenable<any> {
    let newSelections = [...this.st.mealSelections!!];
    newSelections[idx] = quantity;
    let meal = this.st.menu!![idx].name;
    this.ust(st => ({...st,
                     newMeals: quantity > 0 && !st.originMealSelections[idx]
                                        ? [...st.newMeals.filter(x => x != meal), meal]
                                        : [...st.newMeals.filter(x => x != meal)],
                     mealSelections: newSelections}));
    return resolved(1);
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI, CustomerOrderBuilderI]
    = this.c4(T, TitleStdMainMenu, Loading, CustomerOrderBuilder);

    return <div>
      <TitleStdMainMenuI t$title="Order update"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/visitor/balance/${p.fid}`}
                                           t$label="My balance" />,
                           <NavbarLinkItem path={`/festival/visitor/orders/${p.fid}`}
                                           t$label="my orders" />
                         ]}/>
      {(!st.menu || !st.originMealSelections ||  st.currentBalance === U) && !st.e && <LoadingI/>}
      <RestErrCo e={st.e} />
      {!!st.menu && !!st.originMealSelections && st.currentBalance !== U && <CustomerOrderBuilderI
         menu={st.menu!!}
         mealSelections={st.mealSelections!!}
         newMeals={st.newMeals}
         missingMeals={st.missingMeals}
         currentBalance={st.currentBalance + sumMealsPrice(st.menu!!, st.originMealSelections!!) }
         onPutOrder={this.putOrder}
         validationError={
           !!st.updateOutcome && <div class={jne(bulma.message, bulma.isDanger)}>
             <div class={bulma.messageHeader}>
               {st.updateOutcome == 'RETRY' && <p><TI m="Try again"/></p> }
               {st.updateOutcome == 'FESTIVAL_OVER' && <p><TI m="Festival is over"/></p> }
               {st.updateOutcome == 'BAD_ORDER_STATE' && <p><TI m="Order state does not support modification"/></p> }
               {st.updateOutcome == 'NOT_ENOUGH_FUNDS' && <p><TI m="Not enough money to pay new version of order"/></p>}
             </div>
           </div>
         }
         onMealSelected={this.mealSelected} />}
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerOrderModification> {
    return regBundleCtx(bundleName, mainContainer, CustomerOrderModification,
      o => o.bind([
        ['festMenuSr', FestMenuSr],
        ['orderSr', OrderSr],
        ['tokenSr', TokenSr],
      ]) as FwdContainer);
}
