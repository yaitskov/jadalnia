import { postJ, geT } from 'async/abortable-fetch';
import { Thenable } from 'async/abortable-promise';
import { Fid } from 'app/page/festival/festival-types';
import { MenuItemView } from 'app/service/fest-menu-types';

export class FestMenuSr {
  public list(fid: Fid): Thenable<MenuItemView[]> {
    return geT(`/api/festival/menu/${fid}`).tn(
      r => r.json().then(lst => lst));
  }
}
