import { h } from 'preact';
import { Container, FwdContainer } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { Fid } from 'app/page/festival/festival-types';
import { FestMenuSr } from 'app/service/fest-menu-service';
import { MenuItemView } from 'app/service/fest-menu-types';
import { If } from 'component/if';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import {AddMenuItemBtn} from "app/page/festival/menu/add-menu-item-btn";
import { TransCom, TransComS } from 'i18n/trans-component';
import {nic, Opt, opt} from 'collection/optional';
import bulma from "app/style/my-bulma.sass";

interface FestMenuS extends TransComS {
  items: Opt<MenuItemView[]>
}

class FestMenu extends TransCom<{fid: Fid}, FestMenuS> {
  // @ts-ignore
  private $festMenuSr: MenuService;

  constructor(props) {
    super(props);
    this.st = {items: nic(), at: this.at()};
  }

  wMnt() {
    this.$festMenuSr.list(this.props.fid).tn(
      lst => this.ust(st => ({...st, items: opt(lst)})));
  }

  render() {
    const [AddMenuItemBtnI, TitleStdMainMenuI] = this.c2(AddMenuItemBtn, TitleStdMainMenu);
    return <div>
      <TitleStdMainMenuI t$title="Welcome to FoodFest"/>
      <AddMenuItemBtnI fid={this.props.fid} />
      <section>
        <ul class={bulma.list}>
          <If f={this.st.items.empty}>
            <li class={bulma.listItem}>loading</li>
          </If>
          <If f={this.st.items.map(l => l.length).el(1) == 0}>
            <li class={bulma.listItem}>menu is empty</li>
          </If>
          {this.st.items.el([])
            .map(i => <li class={bulma.listItem}>{i.name} / {i.price}</li>)}
        </ul>
      </section>
      <AddMenuItemBtnI fid={this.props.fid} />
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<FestMenu> {
  return regBundleCtx(
    bundleName, mainContainer, FestMenu,
    (o) => o.bind([
      ['festMenuSr', FestMenuSr]
    ]) as FwdContainer);
}
