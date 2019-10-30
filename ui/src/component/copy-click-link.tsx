import { h } from 'preact';
import {Link} from "preact-router";
import {MyCo} from "component/my-component";

export interface CpClickLnkP {
  url: string;
  t$lbl: string;
  classes?: string;
}

export class CpClickLnk extends MyCo<CpClickLnkP, {}> {
  constructor(props) {
    super(props);
    this.onClick = this.onClick.bind(this);
  }

  onClick(e) {
    e.preventDefault();
    e.stopImmediatePropagation();
    return false;
  }

  render(p) {
    return <Link href={p.url}
                 title="click to copy"
                 class={p.classes || ''}
                 onClick={this.onClick}>
      { p.t$lbl }
    </Link>;
  }
}
