import { h } from 'preact';
import { Link } from 'preact-router';

import { MyCo } from 'component/my-component';
import bulma from 'bulma/bulma.sass';

export interface NavbarLinkItemP {
  t$label: string;
  path: string;
}

export class NavbarLinkItem extends MyCo<NavbarLinkItemP, {}> {
  render() {
    return <Link activeClassName="active"
                 class={bulma.navbarItem}
                 href={this.props.path}>
      {this.props.t$label}
    </Link>;
  }
}
