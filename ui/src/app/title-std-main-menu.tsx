import { h } from 'preact';
import { TitleMainMenu } from 'app/component/title-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { NavbarLinkItem } from 'app/component/navbar-link-item';

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
    return <TitleMainMenuI
      t$title={this.props.t$title}
      menuItems={
        [
          <NavbarLinkItem path="/draft" t$label="Draft to coming tournaments" />,
          <NavbarLinkItem path="/watch" t$label="Watch ongoing tournaments" />,
          <hr class={bulma.navbarDivider}/>
        ]
      } />;
  }

  at(): string[] { return []; }
}
