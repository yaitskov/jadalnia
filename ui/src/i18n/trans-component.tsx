import { h } from 'preact';
import { InjSubCom } from 'injection/inject-sub-components';
import { I18Trans } from 'i18n/i18n-translator';

export interface TransComS {
  at: string[];
}

export abstract class TransCom<P, S extends TransComS> extends InjSubCom<P, S> {
  // @ts-ignore
  protected $i18Trans: I18Trans;

  protected wMnt() {
    this.bulkTrans();
  }

  protected bulkTrans() {
    this.$i18Trans.bulkMap(this.st.at).tn(t => this.ust(st => ({...st, at: t})));
  }

  protected abstract at(): string[];
}
