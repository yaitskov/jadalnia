import { InjSubCom } from "injection/inject-sub-components";
import { T } from "i18n/translate-tag";
import {h} from "preact";
import {Link} from "preact-router";
import bulma from "app/style/my-bulma.sass";
import { Fid } from "app/page/festival/festival-types";

export class AddMenuItemBtn extends InjSubCom<{fid: Fid}, {}> {
  render() {
    const TI = this.c(T);
    return <div class={bulma.buttons + ' ' + bulma['is-centered']}>
        <Link class={bulma.button + ' ' + bulma['is-primary']}
              href={"/admin/festival/menu/new/item/" + this.props.fid}>
          <TI m="Add item"/>
        </Link>
      </div>;
  }
}
