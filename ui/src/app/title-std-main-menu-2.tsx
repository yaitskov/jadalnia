import { h } from 'preact';
import { TitleMainMenu2 } from 'app/component/title-main-menu-2';
import { TransCom, TransComS } from 'i18n/trans-component';
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import { Admin, UserAuth } from "app/auth/user-auth";
import bulma from 'bulma/bulma.sass';
import {SuperElement} from "component/types";

export interface TitleStdMainMenuP2 {
  title: SuperElement | string;
  extraItems?: SuperElement[];
}

export class TitleStdMainMenu2 extends TransCom<TitleStdMainMenuP2, TransComS> {
  // @ts-ignore
  private $userAuth: UserAuth;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p) {
    const TitleMainMenu2I = this.c(TitleMainMenu2);
    let items = [...(p.extraItems || [])];
    if (this.$userAuth.userType() == Admin) {
      if (items.length > 0) {
        items.push(<hr class={bulma.navbarDivider}/>);
      }
      this.$userAuth.myFid().ifV(fid => items.unshift(
        <NavbarLinkItem path={`/admin/festival/control/${fid}`}
                        t$label="Fest control" />))
     }
    return <TitleMainMenu2I title={this.props.title} menuItems={items} />;
  }

  at(): string[] { return []; }
}
