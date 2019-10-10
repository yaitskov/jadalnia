import { h } from 'preact';
import { InjSubCom } from 'injection/inject-sub-components';
import { Container, FwdContainer } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { LocalStorage } from 'app/persistence/local-storage';
import { NewFestival, Fid } from 'app/page/festival/festival-types';
import { FestMenuSr } from 'app/service/fest-menu-service';

class FestMenuCom extends InjSubCom<{fid: Fid}, {}> {
  // @ts-ignore
  private $locStore: LocalStorage;
  // @ts-ignore
  private $festMenuSr: MenuService;

  wMnt() {
    // this.$festMenuSr.list(
  }

  render() {
    return <div>fid =  {this.props.fid }</div>;
    /* return this.$menuSr.list()
    *   .map(menuItems =>
     *     <div>
     *       <p>menu stub</p>
     *     </div>)
     *   .elf(() => <div>menu empty</div>); */
  }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<FestMenuCom> {
  return regBundleCtx(
    bundleName, mainContainer, FestMenuCom,
    (o) => o.bind([
      ['festMenuSr', FestMenuSr]
    ]) as FwdContainer);
}
