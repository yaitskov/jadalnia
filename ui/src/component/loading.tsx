import { h } from 'preact';
import { InjSubCom } from "injection/inject-sub-components";
import { T } from 'i18n/translate-tag';

export interface LoadingP {
  classes?: string;
  t$lbl?: string;
}

export class Loading extends InjSubCom<LoadingP, {}> {
  render(p) {
    const TI = this.c(T);
    return <span class={p.classes || ''}>
      { !!p.t$lbl && p.t$lbl }
      { !p.t$lbl && <TI m="Loading..." />}
    </span>;
  }
}
