import { h } from 'preact';
import { U } from 'util/const';
import { MyCo } from 'component/my-component';
import { Valiform, FormLevel } from 'component/form/validation/form-validation';
import { InputOkP, InputIf } from 'component/form/validation/input-if';
import { Invalid } from 'component/form/validation/invalid';

import bulma from 'bulma/bulma.sass';
import { opt } from 'collection/optional';

interface InputOkS {
  val: string;
}

export class InputOk extends MyCo<InputOkP, InputOkS> implements InputIf {
  // @ts-ignore
  $valiform: Valiform;
  // @ts-ignore
  form: FormLevel;

  constructor(props) {
    super(props);
    this.state = {val: ''};
    this.onBlur = this.onBlur.bind(this);
    this.onChng = this.onChng.bind(this);
    this.onKeyUp = this.onKeyUp.bind(this);
  }

  chkN(): string {
    return U;
  }

  public getProps(): InputOkP {
    return this.props;
  }

  wMnt() {
    this.form = this.$valiform.topForm();
    this.form.add(this);
  }

  wUmt() {
    this.form.rm(this);
  }

  onKeyUp(e) {
    console.log(`kye up ${e.target.value}`);
    // set state some how
    this.form.checkFieldBy(this, ['k'], e.target.value);
  }

  onBlur(e) {
    console.log(`focus lost ${e.target.value}`);
    this.form.checkFieldBy(this, ['k', 'c'], e.target.value);
  }

  onChng(e) {
    console.log(`change ${this.state.val} => ${e.target.value}`);
    e.preventDefault();
    this.form.change(this, this.state.val, e.target.value);
    this.setState({val: e.target.value});
  }

  render(p, st) {
    return <div class={bulma.control}>
      {opt(p.inputFactory).ifVE(
        inpFactory => inpFactory(
          {onBlur: this.onBlur,
            onChng: this.onChng,
            onKeyUp: this.onKeyUp,
            val: st.val}),
        () => <input class={bulma.input} value={st.val}
                     onBlur={this.onBlur}
                     onChange={this.onChng}
                     onKeyUp={this.onKeyUp}/>
      )}
    </div>;
  }

  updateVal(v: string) {
    this.form.change(this, '', v);
    this.setState({val: v});
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
