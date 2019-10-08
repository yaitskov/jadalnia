import { h } from 'preact';
import { jne } from 'collection/join-non-empty';
import { MyCo } from 'component/my-component';
import { Valiform } from 'component/form/validation/form-validation';
import { Invalid } from 'component/form/validation/invalid';
import { ValiFieldLi } from 'component/form/validation/input-if';
import { If } from 'component/if';

import bulma from 'bulma/bulma.sass';

interface IfErrSt {
  show: boolean;
}

interface IfErrP {
  check: string;
}

// show children if field is invalid
export class IfErr extends MyCo<IfErrP, IfErrSt> implements ValiFieldLi {
  // @ts-ignore TS2564
  $valiform: Valiform;

  constructor(props) {
    super(props);
    this.valid();
  }

  public chkN(): string {
    return this.pr.check;
  }

  wMnt() {
    console.log(`iferr will mount ${this.props.check}`);
    this.$valiform.topForm().addFan(this);
  }

  dMnt() {
    console.log(`iferr did mount ${this.props.check}`);
  }

  valid() {
    this.ust(s => ({...s, show: false}));
  }

  invalid(inv: Invalid[]) {
    this.ust(s => ({...s, show: inv.filter(i => i.check == this.chkN()).length > 0}));
  }

  dirty() {
    // nop
  }

  empty() {
    this.valid();
  }

  render() {
    return <If f={this.st.show}>
      <p class={jne(bulma.help, bulma.isDanger)}>
        {
          this.props.children // @ts-ignore
        }
      </p>
    </If>;
  }
}
