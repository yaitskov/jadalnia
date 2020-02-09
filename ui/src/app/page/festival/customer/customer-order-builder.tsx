import { h } from 'preact';

import bulma from 'app/style/my-bulma.sass';

import { SecCon } from 'app/component/section-container';
import { T } from 'i18n/translate-tag';
import { TransCom, TransComS } from 'i18n/trans-component';
import { jne } from "collection/join-non-empty";
import { Loading } from "component/loading";
import { MenuItemView } from "app/service/fest-menu-types";
import { sumMealsPrice } from 'app/types/order';
import { DishName } from "app/types/menu";
import { Thenable } from "async/abortable-promise";

export interface CustomerOrderBuilderP {
  menu: MenuItemView[];
  mealSelections: number[];
  newMeals: DishName[];
  missingMeals: DishName[];
  currentBalance: number;
  onPutOrder: () => Thenable<any>;
  onMealSelected: (idx: number, quantity: number) => Thenable<any>;
}

export interface CustomerOrderBuilderS extends TransComS {
  puttingOrder: boolean;
}

export class CustomerOrderBuilder
  extends TransCom<CustomerOrderBuilderP, CustomerOrderBuilderS> {
  constructor(props) {
    super(props);
    const t = this;
    t.st = {at: t.at(), puttingOrder: false};

    t.putOrder = t.putOrder.bind(t);
    t.toggleItem = t.toggleItem.bind(t);
    t.putOrder = t.putOrder.bind(t);
  }

  putOrder(): Thenable<any> {
    this.ust(st => ({...st, puttingOrder: true}))
    return this.pr.onPutOrder()
      .tn(o => this.ust(st => ({...st, puttingOrder: false})))
      .ctch(e => {
        this.ust(st => ({...st, puttingOrder: false}))
        throw e;
      });
  }

  toggleItem(i: number) {
    this.pr.onMealSelected(i, this.pr.mealSelections[i] ? 0 : 1);
  }

  sumMeals(p): number {
    return sumMealsPrice(p.menu, p.mealSelections);
  }

  addItem(i: number) {
    this.pr.onMealSelected(i, this.pr.mealSelections[i] + 1);
  }

  removeItem(i: number) {
    this.pr.onMealSelected(i, this.pr.mealSelections[i] - 1);
  }

  render(p, st) {
    const [TI, LoadingI] = this.c2(T, Loading);
    const sumMeals = this.sumMeals(p);
    return <SecCon css={bulma.content}>
        <ul class={bulma.list}>
          {p.menu.map((item: MenuItemView, i: number) => <li>
            <p class={jne(bulma.isInfo, !!p.mealSelections[i] && bulma.isPrimary,
               p.newMeals.indexOf(p.menu[i].name) >= 0 && bulma.isSuccess,
               p.missingMeals.indexOf(p.menu[i].name) >= 0 && bulma.strikeThrough)}
               onClick={() => this.toggleItem(i)}>
               {item.name} / {item.price}
            </p>
            { !!p.mealSelections[i] && <div class={bulma.buttons}>
              <button class={bulma.button} onClick={() => this.addItem(i)}>+</button>
              <button class={bulma.button}>{p.mealSelections[i]}</button>
              <button class={bulma.button} onClick={() => this.removeItem(i)}>-</button>
            </div>}
          </li>)}
        </ul>
        { sumMeals == 0 && <div>
          <p>
            <TI m="Choose a meal from the list above to make an order."/>
          </p>
        </div>}
        { sumMeals > 0 && <div>
          <p><TI m="Total:"/></p>
          <p><TI m="Current balance" v={p.currentBalance}/></p>
          <p><TI m="Order cost" v={sumMeals}/></p>
          <p><TI m="Quote to pay" v={Math.max(0, sumMeals - p.currentBalance)}/></p>
          { !st.puttingOrder && <div class={bulma.buttons} >
            <button onClick={this.putOrder}
                    class={jne(bulma.button, bulma.isPrimary, bulma.isCenter )}>
              <TI m="put order" />
            </button>
          </div>}
          { st.puttingOrder && <LoadingI t$lbl="Putting order..." />}
        </div> }
      </SecCon>;
  }

  at(): string[] { return []; }
}
