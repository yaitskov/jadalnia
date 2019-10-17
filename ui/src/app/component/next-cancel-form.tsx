import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';
import { T } from 'i18n/translate-tag';
import { Sform } from 'component/form/sform';
import { Submit } from 'component/form/submit';

import bulma from 'app/style/my-bulma.sass';
import {Thenable} from "async/abortable-promise";
import {goBack} from "util/routing";
import { jne } from 'collection/join-non-empty';
import { RestErrCo } from 'component/err/error';

export interface NextCancelFormP<P> {
  origin: P;
  t$next: string;
  next: (p: P) => Thenable<any>;
}

export interface NextCancelFormS extends TransComS {
  e?: Error;
}

export class NextCancelForm<P>
  extends TransCom<NextCancelFormP<P>, NextCancelFormS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};

  }

  render() {
    class SFormT extends Sform<P> {}
    const [TI, SformTI, SubmitI] = this.c3(T, SFormT, Submit);

    return <SformTI data={this.props.origin}
                    onSend={(d) => this.props.next(d)
                      .tn(result => {
                        this.ust(s => {
                          let {e, ...noE} = s;
                          return noE;
                        });
                        return result;})
                      .ctch(e => this.ust(s => ({...s, e: e})))
                    }>
      {
        this.props.children // @ts-ignore
      }
      <RestErrCo e={this.st.e} />
      <div class={bulma.control}>
        <div class={bulma.buttons}>
          <SubmitI css={bulma.isPrimary} t$text={this.props.t$next} />
          <button class={jne(bulma.button, bulma.isDanger)} onClick={goBack}>
            <TI m="Cancel"/>
          </button>
        </div>
      </div>
    </SformTI>;
  }

  at(): string[] { return []; }
}
