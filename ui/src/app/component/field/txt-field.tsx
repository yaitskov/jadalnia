import { h } from 'preact';
import { CheckOn, ValidatorList } from 'component/form/validation/input-check-def';
import { TransCom, TransComS } from 'i18n/trans-component';
import { InputBox } from 'component/form/input/input-box';
import { InputOk } from 'component/form/input/input-ok';
import { InputCheck } from 'component/form/validation/input-check';
import { InpErr } from 'component/form/validation/input-error';
import { DefaultErr } from 'component/form/validation/default-error';


export interface TxtFieldP {
  name: string;
  t$lbl: string;
  on?: CheckOn; // default on change
  mit?: ValidatorList; // default e! =. not empty
}

export class TxtField extends TransCom<TxtFieldP, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [DefaultErrI, InputBoxI, InputOkI, CheckI] =
      this.c4(DefaultErr, InputBox, InputOk, InputCheck);
    const InpErrI = this.c(InpErr);

    return <InputBoxI>
      <label>
        {this.props.t$lbl}
        <CheckI on={this.props.on || "c"}
                mit={this.props.mit || "!e"}>
          <InputOkI a={this.props.name} />
        </CheckI>
      </label>
      <InpErrI>
        <DefaultErrI/>
      </InpErrI>
    </InputBoxI>;
  }

  at(): string[] { return []; }
}
