import { h } from 'preact';
import { Link } from 'preact-router';
import { InjSubCom } from 'injection/inject-sub-components';
import { T } from 'i18n/translate-tag';
import { SuperElement } from 'component/types';
import { CountryFlag } from 'app/component/country-flag';

import bulma from 'bulma/bulma.sass';

import logo from 'app/icons/logo.png';
import { jne } from 'collection/join-non-empty';

export interface TitleMainMenuP {
  t$title: string;
  menuItems: SuperElement[];
}

interface TitleMainMenuS {
  showMenu: boolean;
}

export class TitleMainMenu extends InjSubCom<TitleMainMenuP, TitleMainMenuS> {
  // @ts-ignore
  private $curLang: ObVar<LanguageCode>;

  toggleMenu() {
    this.ust(st => ({...st, showMenu: !st.showMenu}));
  }

  constructor(props) {
    super(props);
    this.toggleMenu = this.toggleMenu.bind(this);
    this.st = {showMenu: false};
  }

  render() {
    const TI = this.c(T);
    const active = this.st.showMenu ? bulma.isActive : '';
    return <nav class={bulma.navbar} role="navigation" aria-label="main navigation">
      <div class={bulma.navbarBrand}>
        <a class={bulma.navbarItem} href="/">
          <img src={logo} />
        </a>

        <a class={bulma.navbarItem} href="#">
          {this.props.t$title}
        </a>

        <a role="button" class={bulma.navbarBurger} onClick={this.toggleMenu}
           aria-label="menu" aria-expanded="false" data-target="mainMenuAnchor">
          <span aria-hidden="true"></span>
          <span aria-hidden="true"></span>
          <span aria-hidden="true"></span>
        </a>
      </div>

      <div id="mainMenuAnchor" class={bulma.navbarMenu + ' ' + active}>
        {!!this.props.menuItems && !!this.props.menuItems.length &&
        <div class={bulma.navbarStart}>
          <div class={jne(bulma.navbarItem, bulma.hasDropdown, bulma.isHoverable)}>
            <a class={bulma.navbarLink}>
              <TI m="Menu" />
            </a>

            <div class={bulma.navbarDropdown}>
              {this.props.menuItems}
            </div>
          </div>
        </div>}

        <div class={bulma.navbarEnd}>
          <div class={bulma.navbarItem}>
            <div class={bulma.buttons}>
              <Link href="/lang" class={bulma.button + ' ' + bulma.isLight}>
                <CountryFlag code={this.$curLang.val} />
              </Link>
            </div>
          </div>
        </div>
      </div>
    </nav>;
  }
}
