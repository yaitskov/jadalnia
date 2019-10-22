import { geT } from 'async/abortable-fetch';
import { Thenable } from 'async/abortable-promise';
import { Fid } from 'app/page/festival/festival-types';
import { MenuItemView, FestMenuItemFull, emptyFestMenuItem } from 'app/service/fest-menu-types';
import { RestSr } from "app/service/rest-service";

export class FestMenuSr {
  // @ts-ignore
  private $restSr: RestSr;

  list(fid: Fid): Thenable<MenuItemView[]> {
    return geT(`/api/festival/menu/${fid}`).tn(
        r => r.json().then(lst => lst));
  }

  newFestMenuItem(): FestMenuItemFull {
    return emptyFestMenuItem();
  }

  addItemToCurrentMenu(fid: Fid, item: FestMenuItemFull): Thenable<boolean> {
    return this.list(fid).tn(
      menuItems => this.$restSr.postJ<number>("/api/festival/menu", [...menuItems, item])
        .tn(updated => updated > 0));
  }

  updateItemToCurrentMenu(fid: Fid, oldName: string, item: FestMenuItemFull): Thenable<boolean> {
    return this.list(fid).tn(
      menuItems => {
        let idx = menuItems.findIndex(oldItem => oldItem.name == oldName);
        if (idx < 0) {
          throw Error("item removed/renamed. reload menu");
        }
        menuItems[idx] = item;
        return this.$restSr.postJ<number>("/api/festival/menu", menuItems)
          .tn(updated => updated > 0);
      }
    )
  }
}
