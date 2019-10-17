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

export const ValidPhoneRegex = '^[+]?([0-9]+[\\s-]?)+$';

export class PhoneField extends InjSubCom<{}, {}> {
  render() {
    const [DefaultErrI, InputBoxI, InputOkI, CheckI] =
      this.c4(DefaultErr, InputBox, InputOk, InputCheck);
    const [TI, InpErrI, IfErrI, InpHintI] = this.c4(T, InpErr, IfErr, InpHint);

    return <InputBoxI>
      <label class={bulma.label}>
        <TI m="Phone" />
        <CheckI on="c" mit={`r:${ValidPhoneRegex}`}>
          <InputOkI a="phone" />
        </CheckI>
      </label>
      <InpErrI>
        <IfErrI check="r">phone has invalid format</IfErrI>
        <DefaultErrI/>
      </InpErrI>
      <InpHintI>
        <p>phone is optional on sign-up</p>
      </InpHintI>
    </InputBoxI>;
  }
}
