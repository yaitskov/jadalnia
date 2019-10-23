import { Invalid } from 'component/form/validation/invalid';

export type FieldName = string;

export interface InputP {
  val: string;
  onChng: (e: any) => void;
  onKeyUp: (e: any) => void;
  onBlur: (e: any) => void;
}

export interface InputOkP {
  style?: string;
  cls?: string;
  a: FieldName;
  inputFactory?: (params: InputP) => any;
}

export interface ValiFieldLi {
  valid();
  invalid(inv: Invalid[]);
  dirty();
  empty();
  chkN(): string;
}

export interface InputIf extends ValiFieldLi {
  getProps(): InputOkP;
  updateVal(v: string);
}
