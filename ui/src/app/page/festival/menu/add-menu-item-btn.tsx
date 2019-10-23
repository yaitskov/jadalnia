import { InjSubCom } from "injection/inject-sub-components";
import { T } from "i18n/translate-tag";
import {h} from "preact";
import {Link} from "preact-router";
import bulma from "app/style/my-bulma.sass";
import { Fid } from "app/page/festival/festival-types";

export class AddMenuItemBtn extends InjSubCom<{fid: Fid}, {}> {
  render(p) {
    const TI = this.c(T);
    return <div class={bulma.buttons + ' ' + bulma.isCentered}>
      <Link class={bulma.button + ' ' + bulma.isPrimary}
            href={`/admin/festival/menu/new/item/${p.fid}`}>
        <TI m="Add item"/>
      </Link>
      <Link class={bulma.button + ' ' + bulma.isSuccess}
            href={`/admin/festival/new/invites/${p.fid}`}>
        <TI m="Next/invites"/>
      </Link>
    </div>;
  }
}
