import { h } from 'preact';

import { DishName } from 'app/types/menu';
import { jne } from 'collection/join-non-empty';
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {RestErrCo} from "component/err/error";
import {TransCom, TransComS} from "i18n/trans-component";
import {SecCon} from "app/component/section-container";
import {TitleStdMainMenu} from "app/title-std-main-menu";
import bulma from 'app/style/my-bulma.sass';
import {Loading} from "component/loading";
import {OrderSr} from "app/service/order-service";
import { T } from 'i18n/translate-tag';
import { U } from 'util/const';
import { Fid } from 'app/page/festival/festival-types';
import {Thenable} from "async/abortable-promise";
import { NavbarLinkItem } from 'app/component/navbar-link-item';

export interface KelnerMissingMealsS extends TransComS {
  missingMeals?: DishName[];
  e?: Error;
}

class KelnerMissingMeals extends TransCom<{fid: Fid}, KelnerMissingMealsS> {
  // @ts-ignore
  $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};

    this.markAvailable = this.markAvailable.bind(this);
  }

  wMnt() {
    this.$orderSr.listUnavailableMeals()
      .tn(meals => this.ust(st => ({...st, missingMeals: meals})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  markAvailable(meal: DishName) {
    this.ust(st => ({...st, missingMeals: U}));
    this.$orderSr.mealAvailable(meal)
      .tn(ok => this.$orderSr.listUnavailableMeals()
                             .tn(meals => this.ust(st => ({...st, missingMeals: meals}))))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [TitleStdMainMenuI, LoadingI, TI]
      = this.c3(TitleStdMainMenu, Loading, T);

    return <div>
      <TitleStdMainMenuI t$title="Missing meals"
                         extraItems={[
                           <NavbarLinkItem path={`/festival/kelner/serve/${p.fid}`}
                                           t$label="Kelner service" />
                         ]}/>
      <SecCon css={bulma.content}>
        <RestErrCo e={this.st.e} />
        {st.missingMeals === U && !st.e && <LoadingI/>}
        {!!st.missingMeals && !st.missingMeals.length && <div
            class={jne(bulma.message, bulma.isInfo)}>
          <div class={bulma.messageHeader}>
            <TI m="All means are available"/>
          </div>
        </div>}
        {!!st.missingMeals && !!st.missingMeals.length && !st.e && <ul class={bulma.list}>
          {st.missingMeals.map(meal => <li class={bulma.listItem}>
                <label class={bulma.label}>
                  <button class={jne(bulma.button, bulma.isDanger)}
                          onClick={() => this.markAvailable(meal)}>
                    x
                  </button>
                  {meal}
                </label>
            </li>)
          }
        </ul>}
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}


export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KelnerMissingMeals> {
  return regBundleCtx(bundleName, mainContainer, KelnerMissingMeals,
      o => o.bind([['orderSr', OrderSr]]) as FwdContainer);
}
