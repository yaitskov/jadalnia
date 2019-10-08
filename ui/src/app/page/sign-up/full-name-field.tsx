import { h } from 'preact';
import { InputBox } from 'component/form/input/input-box';
import { InputOk } from 'component/form/input/input-ok';
import { InputCheck } from 'component/form/validation/input-check';
import { InpErr } from 'component/form/validation/input-error';
import { IfErr } from 'component/form/validation/if-error';
import { DefaultErr } from 'component/form/validation/default-error';
import { InjSubCom } from 'injection/inject-sub-components';
import { T } from 'i18n/translate-tag';

import bulma from 'bulma/bulma.sass';

export class FullNameField extends InjSubCom<{}, {}> {
  render() {
    const [DefaultErrI, InputBoxI, InputOkI, CheckI] =
      this.c4(DefaultErr, InputBox, InputOk, InputCheck);
    const [TI, InpErrI, IfErrI] = this.c3(T, InpErr, IfErr);

    return <InputBoxI>
      <label class={bulma.label}>
        <TI m="Fullname" />
        <CheckI on="c" mit="!e rng:3:40">
          <InputOkI a="fullName" />
        </CheckI>
      </label>
      <InpErrI>
        <IfErrI check="!e">fullname is required</IfErrI>
        <DefaultErrI/>
      </InpErrI>
    </InputBoxI>;
  }
}
