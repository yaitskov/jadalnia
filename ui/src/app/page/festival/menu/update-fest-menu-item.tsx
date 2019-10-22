import { h } from 'preact';
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { FestMenuItemForm } from 'app/page/festival/menu/fest-menu-item-form';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import { Opt, nic, opt } from 'collection/optional';
import {FestMenuItemFull, emptyFestMenuItem, MenuItemView} from 'app/service/fest-menu-types';

import {goBack} from "util/routing";
import {FestMenuSr} from "app/service/fest-menu-service";
import {Thenable} from "async/abortable-promise";
import {Fid} from "app/page/festival/festival-types";

export interface FestMenuItemS extends TransComS {
  menuItem: Opt<FestMenuItemFull>
  items: Opt<MenuItemView[]>
}

class UpdateFestMenuItem extends TransCom<{fid: Fid, name: string}, FestMenuItemS> {
  // @ts-ignore
  private $festMenuSr: FestMenuSr;

  constructor(props) {
    super(props);
    this.st = {
      items: nic(),
      menuItem: nic(),
      at: this.at()
    };
  }

  wMnt() {
    this.$festMenuSr.list(this.props.fid).tn(
      lst => this.ust(st => ({...st,
          menuItem: opt(lst.find(item => item.name == this.props.name)),
          items: opt(lst)})))
  }

  updateMenu(item: FestMenuItemFull): Thenable<void> {
    return this.$festMenuSr.updateItemToCurrentMenu(this.props.fid, this.props.name, item)
      .tn(success => goBack());
  }

  render() {
    const [TI, TitleStdMainMenuI, FestMenuItemFormI]
    = this.c3(T, TitleStdMainMenu, FestMenuItemForm);
    return <div>
      <TitleStdMainMenuI t$title="Update menu item"/>
      <SecCon>
        {
          this.st.menuItem.ifVE(
            item => <FestMenuItemFormI menuItem={item}
                                       t$submitLabel="Update"
                                       onSubmit={item => this.updateMenu(item)} />,
            () => this.st.items.ifVE(
              items => <div>item has been removed</div>,
              () => <div>loading...</div>))
        }
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(
    bundleName: string, mainContainer: Container)
    : Instantiable<UpdateFestMenuItem> {
  return regBundleCtx(bundleName, mainContainer, UpdateFestMenuItem,
    (o) => o.bind([
      ['festMenuSr', FestMenuSr]
    ]) as FwdContainer);
}
