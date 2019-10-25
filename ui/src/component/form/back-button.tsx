import { h } from 'preact';
import {goBack} from "util/routing";
import bulma from 'app/style/my-bulma.sass';
import {MyCo} from "component/my-component";
import { jne } from "collection/join-non-empty";

export interface BackBtnP {
  classes?: string;
  t$lbl?: string;
}

export class BackBtn extends MyCo<BackBtnP, {}> {
  render(p) {
    return <button class={jne(bulma.button, p.classes || '')} onClick={goBack}>
      { p.t$lbl || '<=' }
    </button>;
  }
}
