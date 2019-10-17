import { RestErr } from "component/err/error-types";
import { MyCo } from 'component/my-component';
import {h} from "preact";
import bulma from "app/style/my-bulma.sass";


export interface RestErrP {
  e?: Error;
}

export class RestErrCo extends MyCo<RestErrP, {}> {
  render() {
    let e = this.props.e;
    if (e instanceof RestErr) {
      return <div class={bulma.field}>
        {e.id && <h4 class={bulma.title + ' ' + bulma.is7}>Error/{e.id}</h4>}
        {e.fmt == 'raw' && <pre class={bulma.hasTextDanger}>{e.message}</pre>}
        {e.fmt == 'tpl' && <div class={bulma.hasTextDanger}>{e.message}</div>}
      </div>;
    } else if (e instanceof Error) {
      return <div class={bulma.field}>
        <pre class={bulma.hasTextDanger}>{e.message}</pre>
      </div>;
    }
    return [];
  }
}
