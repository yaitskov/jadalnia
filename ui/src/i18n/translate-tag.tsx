import { h } from 'preact';
import { MyCo } from 'component/my-component';

import { I18Trans } from 'i18n/i18n-translator';
import { evalTpl, EvalParams } from 'i18n/interpolate';
import { Tobj, deepEq } from 'collection/typed-object';

export type TP = EvalParams;

export interface TS {
  msg: string;
}

export class T extends MyCo<TP, TS> {
  // @ts-ignore
  private $i18Trans: I18Trans;

  constructor(props) {
    super(props);
    this.st = {msg: this.props.m as string};
  }

  wMnt() {
    this.u(this.props.m as string, this.props);
  }

  u(m: string, params: TP) {
    this.ust(s => ({msg: m}));
    this.$i18Trans.map(m).tn(transMsg => this.ust(s => ({msg: evalTpl(transMsg, params)})));
  }

  onUp(prevProps: TP) {
    if (!deepEq(this.props, prevProps)) {
      this.u(this.props.m as string, this.props);
    }
  }

  render() {
    return <span data-text-id={this.props.m}>{this.st.msg}</span>;
  }
}
