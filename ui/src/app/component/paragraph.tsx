import { h } from 'preact';
import { MyCo } from 'component/my-component';

import bulma from 'app/style/my-bulma.sass';

export class Par extends MyCo<{}, {}> {
  render() {
    // @ts-ignore
    return <p class={bulma.content + ' ' + bulma.isMedium}>{this.props.children}</p>;
  }
}
