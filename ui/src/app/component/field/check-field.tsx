import { h } from 'preact';
import {InjSubCom} from "injection/inject-sub-components";
import { CheckBox } from "component/form/input/check-box";
import { InputBox } from "component/form/input/input-box";
import {FieldName} from "component/form/validation/input-if";

export interface CheckFieldP {
  a: FieldName;
  t$ylbl: string;
  t$nlbl: string;
}

export class CheckField extends InjSubCom<CheckFieldP, {}> {
  render(p) {
    const [InputBoxI, CheckBoxI] = this.c2(InputBox, CheckBox);
    return <InputBoxI>
      <CheckBoxI a={p.a} t$ylbl={this.props.t$ylbl} t$nlbl={this.props.t$nlbl} />
    </InputBoxI>;
  }
}
