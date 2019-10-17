import { Invalid } from 'component/form/validation/invalid';

type FieldName = string;

export interface InputOkP {
  style?: string;
  cls?: string;
  a: FieldName;
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
