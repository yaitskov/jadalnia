import { h } from 'preact';
import { U } from 'util/const';
import { MyCo } from 'component/my-component';
import { Valiform, FormLevel } from 'component/form/validation/form-validation';
import { InputOkP, InputIf } from 'component/form/validation/input-if';
import { Invalid } from 'component/form/validation/invalid';
import { DayTimePickr } from 'component/form/input/day-time-pickr';

import bulma from 'bulma/bulma.sass';

interface DayTimeInputP extends InputOkP {
  min?: string | Date;
  css?: string;
}

interface DayTimeInputS {
  val: string;
}

export class DayTimeInput extends MyCo<DayTimeInputP, DayTimeInputS> implements InputIf {
  // @ts-ignore
  $valiform: Valiform;
  // @ts-ignore
  form: FormLevel;

  constructor(props) {
    super(props);
    this.onChng = this.onChng.bind(this);
    this.state = {val: ''};
  }

  public getProps(): InputOkP {
    return this.props;
  }

  chkN(): string {
    return U;
  }

  wMnt() {
    this.form = this.$valiform.topForm();
    this.form.add(this);
  }

  wUmt() {
    this.form.rm(this);
  }

  onChng(lastDate: string) {
    this.updateVal(lastDate);
  }

  updateVal(v: string) {
    this.form.change(this, this.st.val, v);
    this.st = {val: v};
  }

  render() {
    return <div class={bulma.control}>
      <DayTimePickr onChng={this.onChng}
                    fmt="yyyy-MM-ddTHH:ii:ss.SSSZ"
                    mode="DayTime"
                    css={this.props.css}
                    val={this.st.val} />
    </div>;
  }

  empty() {
  }

  valid() {
  }

  dirty() {
  }

  invalid(er: Invalid[]) {
  }
}
