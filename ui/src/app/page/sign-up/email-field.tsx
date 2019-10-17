import { h } from 'preact';
import { InputBox } from 'component/form/input/input-box';
import { InputOk } from 'component/form/input/input-ok';
import { InputCheck } from 'component/form/validation/input-check';
import { InpErr } from 'component/form/validation/input-error';
import { IfErr } from 'component/form/validation/if-error';
import { InpHint } from 'component/form/input/input-hint';
import { DefaultErr } from 'component/form/validation/default-error';
import { InjSubCom } from 'injection/inject-sub-components';
import { T } from 'i18n/translate-tag';

import bulma from 'bulma/bulma.sass';

export const ValidEmailRegex = '^[_A-Za-z0-9]+([.][_A-Za-z0-9]+)*@[A-Za-z0-9-]+([.][A-Za-z0-9-]+)*([.][A-Za-z]{2,4})$';

export class EmailField extends InjSubCom<{}, {}> {
  render() {
    const [DefaultErrI, InputBoxI, InputOkI, CheckI] =
      this.c4(DefaultErr, InputBox, InputOk, InputCheck);
    const [TI, InpErrI, IfErrI, InpHintI] = this.c4(T, InpErr, IfErr, InpHint);

    return <InputBoxI>
      <label class={bulma.label}>
        <TI m="Email" />
        <CheckI on="c" mit={`r:${ValidEmailRegex}`}>
          <InputOkI a="email" />
        </CheckI>
      </label>
      <InpErrI>
        <IfErrI check="r">email has invalid format</IfErrI>
        <DefaultErrI/>
      </InpErrI>
      <InpHintI>
        <p>email is optional on sign-up</p>
      </InpHintI>
    </InputBoxI>;
  }
}
