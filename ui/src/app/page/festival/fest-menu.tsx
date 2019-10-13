import { h } from 'preact';
import { Container, FwdContainer } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { Fid } from 'app/page/festival/festival-types';
import { FestMenuSr } from 'app/service/fest-menu-service';
import { MenuItemView } from 'app/service/fest-menu-types';
import { If } from 'component/if';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { SecCon } from 'app/component/section-container';
import {AddMenuItemBtn} from "app/page/festival/menu/add-menu-item-btn";
import { TransCom, TransComS } from 'i18n/trans-component';

interface FestMenuS extends TransComS {
  items: MenuItemView[]
}

class FestMenu extends TransCom<{fid: Fid}, FestMenuS> {
  // @ts-ignore
  private $festMenuSr: MenuService;

  constructor(props) {
    super(props);
    this.st = {items: [], at: this.at()};
  }

  wMnt() {
    this.$festMenuSr.list(this.props.fid).tn(lst => this.st.items = lst);
  }

  render() {
    const [AddMenuItemBtnI, TitleStdMainMenuI] = this.c2(AddMenuItemBtn, TitleStdMainMenu);
    return <div>
      <TitleStdMainMenuI t$title="Welcome to FoodFest"/>
      <AddMenuItemBtnI fid={this.props.fid} />
      <SecCon>
        <If f={!this.st.items}>
          <div>loading</div>
        </If>
        <If f={this.st.items && !this.st.items.length}>
          <div>menu is empty</div>
        </If>
        {this.st.items.map(item => <div>{item.name} / {item.price}</div>)}
      </SecCon>
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
