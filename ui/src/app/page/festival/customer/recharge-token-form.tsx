import { h } from 'preact';
import { route } from 'preact-router';

import { T } from 'i18n/translate-tag';
import { TransCom, TransComS } from 'i18n/trans-component';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";
import { NextCancelForm } from 'app/component/next-cancel-form';
import {TokenSr} from "app/service/token-service";
import {Thenable} from "async/abortable-promise";

import bulma from 'app/style/my-bulma.sass';
import { TxtField } from 'app/component/field/txt-field';
import {OrderLabel} from "app/types/order";

export interface RechargeTokenFormP {
  fid: Fid;
  order?: OrderLabel;
  minQuote: number;
}

interface RechargeFormData {
  amount: number
}

export interface RechargeTokenFormS extends TransComS {
  form: RechargeFormData;
  progress: boolean;
  e?: Error;
}

export class RechargeTokenForm extends TransCom<RechargeTokenFormP, RechargeTokenFormS> {
  // @ts-ignore
  private $tokenSr: TokenSr;

  constructor(props) {
    super(props);
    this.st = {
      at: this.at(),
      progress: false,
      form: {amount: props.minQuote}
    };
    this.onSubmit = this.onSubmit.bind(this);
  }

  onSubmit(form: RechargeFormData): Thenable<any> {
    this.ust(st => ({...st, progress: true}));
    return this.$tokenSr.requestTokens(form.amount)
      .tn(tokenRequestId => {
        route(`/festival/visitor/token-request/${this.pr.fid}/${tokenRequestId}/${this.pr.order}`);
      })
      .ctch(e => this.ust(st => ({...st, e: e, progress: false})));
  }

  render(p, st) {
    class NextCancelFormT extends NextCancelForm<RechargeFormData> {}
    const [TI, NextCancelFormTI, TxtFieldI] = this.c3(T, NextCancelFormT, TxtField);

    return <NextCancelFormTI t$next="Request tokens"
                             origin={st.form}
                             next={this.onSubmit}>
      <p>
        {!st.progress && <TI m="Recharge your account at least by n tokens." n={p.minQuote} /> }
        {st.progress && <TI m="Requesting tokens ..." /> }
      </p>
      <div class={bulma.field}>
        <TxtFieldI t$lbl="Enter number of tokens to request"
                   name="amount"
                   mit={`!e min:${p.minQuote}`} />
      </div>
      <RestErrCo e={st.e} />
    </NextCancelFormTI>;
  }

  at(): string[] { return []; }
}
