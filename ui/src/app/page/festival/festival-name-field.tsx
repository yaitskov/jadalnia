import { h } from 'preact';
import { InputBox } from 'component/form/input/input-box';
import { InputOk } from 'component/form/input/input-ok';
import { InputCheck } from 'component/form/validation/input-check';
import { InpErr } from 'component/form/validation/input-error';
import { DefaultErr } from 'component/form/validation/default-error';
import { InjSubCom } from 'injection/inject-sub-components';
import { T } from 'i18n/translate-tag';


export class TourNameField extends InjSubCom<{}, {}> {
  render() {
    const [DefaultErrI, InputBoxI, InputOkI, CheckI] =
      this.c4(DefaultErr, InputBox, InputOk, InputCheck);
    const [InpErrI, TI] = this.c2(InpErr, T);

    return <InputBoxI>
      <label>
        <TI m="Name"/>
        <CheckI on="c" mit="rngr:3:120 !e">
          <InputOkI a="name" />
        </CheckI>
      </label>
      <InpErrI>
        <DefaultErrI/>
      </InpErrI>
    </InputBoxI>;
  }
}
