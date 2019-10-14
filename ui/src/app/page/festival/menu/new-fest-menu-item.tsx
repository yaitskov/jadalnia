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
import { FestMenuItemFull, emptyFestMenuItem } from 'app/service/fest-menu-types';

import {goBack} from "util/routing";
import {FestMenuSr} from "app/service/fest-menu-service";
import {Fid} from "../festival-types";

export interface FestMenuItemS extends TransComS {
  menuItem: Opt<FestMenuItemFull>;
}

class NewFestMenuItem extends TransCom<{fid: Fid}, FestMenuItemS> {
  // @ts-ignore
  private $festMenuSr: FestMenuSr;

  constructor(props) {
    super(props);
    this.st = {menuItem: nic(), at: this.at()};
  }

  wMnt() {
    this.st.menuItem = opt(this.$festMenuSr.newFestMenuItem());
  }

  addItem(item: FestMenuItemFull): void {
    this.$festMenuSr.addItemToCurrentMenu(this.props.fid, item)
      .tn(success => {
        if (success) {
          goBack();
        }
      });
  }

  render() {
    const [TI, TitleStdMainMenuI, FestMenuItemFormI]
    = this.c3(T, TitleStdMainMenu, FestMenuItemForm);
    return <div>
      <TitleStdMainMenuI t$title="New menu item"/>
      <SecCon>
        <FestMenuItemFormI menuItem={this.st.menuItem.elf(emptyFestMenuItem)}
                           onSubmit={item => this.addItem(item)} />
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(
    bundleName: string, mainContainer: Container)
    : Instantiable<NewFestMenuItem> {
  return regBundleCtx(bundleName, mainContainer, NewFestMenuItem,
    (o) => o.bind([
      ['festMenuSr', FestMenuSr]
    ]) as FwdContainer);
}
