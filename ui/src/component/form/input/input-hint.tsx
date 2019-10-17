import { h } from 'preact';
import { U } from 'util/const';
import { MyCo } from 'component/my-component';
import { Invalid } from 'component/form/validation/invalid';
import { Valiform } from 'component/form/validation/form-validation';
import { ValiFieldLi } from 'component/form/validation/input-if';
import { If } from 'component/if';

import bulma from 'bulma/bulma.sass';

interface InpHintSt {
  show: boolean;
}

// show children if field is invalid
export class InpHint extends MyCo<{}, InpHintSt> implements ValiFieldLi {
  // @ts-ignore TS2564
  $valiform: Valiform;

  constructor(props) {
    super(props);
    this.state = {show: true};
  }

  chkN(): string {
    return U;
  }

  wMnt() {
    console.log('hint will mount');
    this.$valiform.topForm().addFan(this);
  }

  dMnt() {
    console.log('hint did mount');
  }

  valid() {
    this.setState({show: false});
  }

  invalid(inv: Invalid[]) {
    this.setState({show: true});
  }

  dirty() {
    // nop
  }

  empty() {
    this.setState({show: true});
  }

  render() {
    // @ts-ignore
    return <If f={this.state.show}><p class={bulma.help}>{this.props.children}</p></If>;
  }
}
