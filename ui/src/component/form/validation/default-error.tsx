import { h } from 'preact';
import { jne } from 'collection/join-non-empty';
import { U } from 'util/const';
import { Valiform, MetaInput } from 'component/form/validation/form-validation';
import { Tobj, mapO, emptyM, aHas, toMap } from 'collection/typed-object';
import { Invalid } from 'component/form/validation/invalid';
import { ValiFieldLi } from 'component/form/validation/input-if';
import { MyCo } from 'component/my-component';
import { If } from 'component/if';

import bulma from 'bulma/bulma.sass';

interface St {
  errs: Tobj<string>;
}

export class DefaultErr extends MyCo<{},  St> implements ValiFieldLi {
  // @ts-ignore TS2564
  $valiform: Valiform;
  // @ts-ignore TS2564
  meta: MetaInput;

  constructor(props) {
    super(props);
    this.valid();
  }

  public chkN(): string {
    return U;
  }

  wMnt() {
    const form = this.$valiform.topForm();
    form.addFan(this);
    this.meta = form.curMField();
  }

  valid() {
    console.log(`default error valid`);
    this.st = {errs: {}};
  }

  invalid(inv: Invalid[]) {
    const shownChecks = this.meta.shownChecks();
    console.log(`default error ${inv} ${inv.length}`);
    this.ust(s => ({
      ...s,
      errs: toMap(inv.filter(i => !aHas(i.check, shownChecks)),
                  i => i.check,
                  i => i.msgTmp)
    }));
  }

  dirty() {
    // nop
  }

  empty() {
    this.valid();
  }

  render() {
    console.log('default error render');
    return <If f={!emptyM(this.st.errs)}>
    {
        mapO(this.st.errs,
             ([k, m]) =>
               <p class={jne(bulma.help, bulma.isDanger,  'err-' + k)}>
                 {m}
               </p>)
      }
    </If>;
  }
}
