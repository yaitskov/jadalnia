import { h } from 'preact';
import { U } from 'util/const';
import { Container, inject } from 'injection/inject-1k';
import { Valiform } from 'component/form/validation/form-validation';
import { Invalid } from 'component/form/validation/invalid';
import { ValiFieldLi } from 'component/form/validation/input-if';
import { MyCo } from 'component/my-component';
import { If } from 'component/if';

interface InpErrSt {
  show: boolean;
}

interface InpErrP {
}

// show children if field is invalid
export class InpErr extends MyCo<InpErrP, InpErrSt> implements ValiFieldLi {
  // @ts-ignore TS2564
  $valiform: Valiform;
  // @ts-ignore TS2564
  $container: Container;

  constructor(props) {
    super(props);
    this.st = {show: false};
  }

  chkN(): string {
    return U;
  }

  wMnt() {
    this.$valiform.topForm().addFan(this);
  }

  dMnt() {
    console.log('err did mount');
  }

  valid() {
    console.log(`input error valid`);
    this.st = {show: false};
  }

  invalid(inv: Invalid[]) {
    console.log(`input error invalid`);
    this.ust(s => ({...s, show: true}));
  }

  dirty() {
    // nop
  }

  empty() {
    console.log(`input error empty`);
    this.st = {show: false};
  }

  render() {
    console.log('input error render');
    return <If f={this.st.show} css="errors">
      {
        this.props.children // @ts-ignore
      }
    </If>;
  }
}
