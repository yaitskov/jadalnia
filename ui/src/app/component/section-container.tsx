import { h } from 'preact';
import { jne } from 'collection/join-non-empty';
import { MyCo } from 'component/my-component';

import bulma from 'app/style/my-bulma.sass';

interface SecConP {
  css?: string;
}

export class SecCon extends MyCo<SecConP, {}> {
  render() {
    return <section class={jne(bulma.section, this.props.css)}>
      <div class={bulma.container}>
        {
          this.props.children // @ts-ignore
        }
      </div>
    </section>;
  }
}
