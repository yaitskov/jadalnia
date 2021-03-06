import { h } from 'preact';
import { FieldName } from 'component/form/validation/input-if';
import { InputOk } from 'component/form/input/input-ok';
import {InjSubCom} from "injection/inject-sub-components";
import bulma from 'bulma/bulma.sass';

export interface CheckBoxP {
  a: FieldName;
  t$ylbl: string;
  t$nlbl: string;
}

export interface CheckBoxS {
  checked: boolean;
}

export class CheckBox extends InjSubCom<CheckBoxP, CheckBoxS> {
  render(p, st) {
    const InputOkI = this.c(InputOk);
    return <InputOkI a={p.a} inputFactory={params =>
        <label>
          <input class={bulma.checkbox} type="checkbox" checked={params.val as any} onChange={
            (e) => {
              params.onChng({preventDefault: () => {},
                target: {value: (e.target as any).checked}});
            }
          }/>
          {params.val ? p.t$ylbl : p.t$nlbl}
        </label>
      }/>;
  }
}
