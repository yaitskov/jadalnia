import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';
import { T } from 'i18n/translate-tag';
import { Sform } from 'component/form/sform';
import { Submit } from 'component/form/submit';

import bulma from 'app/style/my-bulma.sass';

export interface NextCancelFormP<P> {
  origin: P;
  t$next: string;
  next: (p: P) => void;
}

export class NextCancelForm<P> extends TransCom<NextCancelFormP<P>, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
    this.cancel = this.cancel.bind(this);
  }

  private cancel(): void {
    window.history.go(-1);
  }

  render() {
    class SFormT extends Sform<P> { };
    const [TI, SformTI, SubmitI] = this.c3(T, SFormT, Submit);

    return <SformTI data={this.props.origin} onSend={this.props.next}>
      {
        this.props.children // @ts-ignore
      }
      <div class={bulma.control}>
        <div class={bulma.buttons}>
          <SubmitI css={bulma.isPrimary} t$text={this.props.t$next} />
          <button class={bulma.button + ' ' + bulma.isDanger} onClick={this.cancel}>
            <TI m="Cancel"/>
          </button>
        </div>
      </div>
    </SformTI>;
  }

  at(): string[] { return []; }
}
