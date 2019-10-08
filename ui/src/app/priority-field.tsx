import { h } from 'preact';
import { InjSubCom } from 'injection/inject-sub-components';

import { InputBox } from 'component/form/input/input-box';
import { InputOk } from 'component/form/input/input-ok';
import { InputCheck } from 'component/form/validation/input-check';
import { InpErr } from 'component/form/validation/input-error';
import { InpHint } from 'component/form/input/input-hint';
import { IfErr } from 'component/form/validation/if-error';

export class PriorityField extends InjSubCom<{}, {}> {
  render() {
    const [InputBoxI, InputOkI, CheckI] = this.c3(InputBox, InputOk, InputCheck);
    const [InpErrI, IfErrI, InpHintI] = this.c3(InpErr, IfErr, InpHint);

    return <InputBoxI>
      <label>
        Priority
        <CheckI on="k" mit="!e i min:1 max:10">
          <InputOkI a="priority" />
        </CheckI>
      </label>
      <InpErrI>
        <IfErrI check="min">priority must be positive</IfErrI>
        <IfErrI check="max">priority must be less than 11</IfErrI>
        <IfErrI check="!e">priority is required</IfErrI>
        <IfErrI check="i">priority must be a number</IfErrI>
      </InpErrI>
      <InpHintI>
        <p>enter priorty for the action</p>
      </InpHintI>
    </InputBoxI>;
  }
}
