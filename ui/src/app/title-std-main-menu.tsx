import { h } from 'preact';
import { TitleMainMenu } from 'app/component/title-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { NavbarLinkItem } from 'app/component/navbar-link-item';
import { Admin, UserAuth } from "app/auth/user-auth";
import bulma from 'bulma/bulma.sass';

export interface TitleStdMainMenuP {
  t$title: string;
}

export class TitleStdMainMenu extends TransCom<TitleStdMainMenuP, TransComS> {
  // @ts-ignore
  private $userAuth: UserAuth;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const TitleMainMenuI = this.c(TitleMainMenu);
    let items = [<hr class={bulma.navbarDivider}/>];
    items.push(<hr class={bulma.navbarDivider}/>);
    if (this.$userAuth.userType() == Admin) {
      this.$userAuth.myFid().ifV(fid => items.unshift(
        <NavbarLinkItem path={`/admin/festival/control/${fid}`}
                        t$label="Fest control" />))
     }
    return <TitleMainMenuI t$title={this.props.t$title} menuItems={items} />;
  }

  at(): string[] { return []; }
}
