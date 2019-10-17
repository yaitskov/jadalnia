import { h } from 'preact';
import { Invalid } from 'component/form/validation/invalid';
import { postJ } from 'async/abortable-fetch';
import { InputBox } from 'component/form/input/input-box';
import { InputOk } from 'component/form/input/input-ok';
import { InputCheck } from 'component/form/validation/input-check';
import { InpErr } from 'component/form/validation/input-error';
import { InpHint } from 'component/form/input/input-hint';
import { IfErr } from 'component/form/validation/if-error';
import { DefaultErr } from 'component/form/validation/default-error';
import { InjSubCom } from 'injection/inject-sub-components';

interface Errors {
  errors: Invalid[];
}

export class ActionField extends InjSubCom<{}, {}> {
  render() {
    const [DefaultErrI, InputBoxI, InputOkI, CheckI] =
      this.c4(DefaultErr, InputBox, InputOk, InputCheck);
    const [InpErrI, IfErrI, InpHintI] =
      this.c3(InpErr, IfErr, InpHint);

    return <InputBoxI>
      <label>
        Action
        <CheckI on="c" mit="r:^.{1,17}$ !e">
          <CheckI on="s" mit={(v) => postJ('/unique-action', {value: v}).tn<Errors>(e => e.json()).tn(e => e.errors) }>
            <InputOkI a="action" />
          </CheckI>
        </CheckI>
      </label>
      <InpErrI>
        <IfErrI check="r">action is too long ;)</IfErrI>
        <IfErrI check="!e">action is required</IfErrI>
        <DefaultErrI/>
      </InpErrI>
      <InpHintI>
        <p>enter descriptive action up to 17 letters</p>
      </InpHintI>
    </InputBoxI>;
  }
}
