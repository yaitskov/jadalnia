import { h } from 'preact';
import { TitleMainMenu } from 'app/component/title-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import { Admin, UserAuth } from "app/auth/user-auth";
import bulma from 'bulma/bulma.sass';
import {SuperElement} from "component/types";

export interface TitleStdMainMenuP {
  t$title: string;
  extraItems?: SuperElement[];
}

export class TitleStdMainMenu extends TransCom<TitleStdMainMenuP, TransComS> {
  // @ts-ignore
  private $userAuth: UserAuth;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p) {
    const TitleMainMenuI = this.c(TitleMainMenu);
    let items = [...(p.extraItems || [])];
    if (this.$userAuth.userType() == Admin) {
      if (items.length > 0) {
        items.push(<hr class={bulma.navbarDivider}/>);
      }
      this.$userAuth.myFid().ifV(fid => items.unshift(
        <NavbarLinkItem path={`/admin/festival/control/${fid}`}
                        t$label="Fest control" />))
    } else if (this.$userAuth.userType() == "Kasier") {
      if (items.length > 0) {
        items.push(<hr class={bulma.navbarDivider}/>);
      }
      this.$userAuth.myFid().ifV(fid => items.unshift(
        <NavbarLinkItem path={`/festival/kasier/serve/${fid}`}
                        t$label="Kasier service" />))
    } else if (this.$userAuth.userType() == "Kelner") {
      if (items.length > 0) {
        items.push(<hr class={bulma.navbarDivider}/>);
      }
      this.$userAuth.myFid().ifV(fid => items.unshift(
        <NavbarLinkItem path={`/festival/kelner/serve/${fid}`}
                        t$label="Kelner service" />))
    }
    return <TitleMainMenuI t$title={this.props.t$title} menuItems={items} />;
  }

  at(): string[] { return []; }
}
