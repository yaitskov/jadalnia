import { h } from 'preact';
import { InjSubCom } from 'injection/inject-sub-components';
import { Container, FwdContainer } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { LocalStorage } from 'app/persistence/local-storage';
import { NewFestival, Fid } from 'app/page/festival/festival-types';
import { FestMenuSr } from 'app/service/fest-menu-service';
import { MenuItemView } from 'app/service/fest-menu-types';
import { If, IfSt } from 'component/if';

class FestMenuCom extends InjSubCom<{fid: Fid}, {items: MenuItemView[]}> {
  // @ts-ignore
  private $locStore: LocalStorage;
  // @ts-ignore
  private $festMenuSr: MenuService;

  wMnt() {
    this.st.items = [];
    this.$festMenuSr.list(this.props.fid).tn(lst => this.st.items = lst);
  }

  render() {
    return <div>
      fid = {this.props.fid}
      <If f={!this.st.items}>
        <div>loading</div>
      </If>
      <If f={this.st.items && !this.st.items.length}>
        <div>menu is empty</div>
      </If>
      {this.st.items.map(item => <div>{item.name} / {item.price}</div>)}
    </div>;
  }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<FestMenuCom> {
  return regBundleCtx(
    bundleName, mainContainer, FestMenuCom,
    (o) => o.bind([
      ['festMenuSr', FestMenuSr]
    ]) as FwdContainer);
}
