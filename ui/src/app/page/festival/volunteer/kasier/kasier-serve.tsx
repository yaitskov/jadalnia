import { h } from 'preact';
import { route } from 'preact-router';

import bulma from 'app/style/my-bulma.sass';

import {Container} from 'injection/inject-1k';
import {regBundle} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Fid, Uid} from 'app/page/festival/festival-types';
import {TransCom, TransComS} from "i18n/trans-component";
import {SecCon} from "app/component/section-container";
import {TitleStdMainMenu} from "app/title-std-main-menu";
import { NextCancelForm } from 'app/component/next-cancel-form';
import {Thenable, resolved} from "async/abortable-promise";
import { TxtField } from 'app/component/field/txt-field';
import { T } from 'i18n/translate-tag';

export interface KasierServeS extends TransComS {
  e?: Error;
}

export interface VisitorLookup {
  uid: Uid;
}

class KasierServe extends TransCom<{fid: Fid}, KasierServeS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
    this.onSubmit = this.onSubmit.bind(this);
  }

  onSubmit(lookup: VisitorLookup): Thenable<any> {
    route(`/festival/kasier/visitor-token-requests/${this.pr.fid}/${lookup.uid}`);
    return resolved(1);
  }

  render(p, st) {
    class VisitorLookupForm extends NextCancelForm<VisitorLookup> {}

    const [TitleStdMainMenuI, VisitorLookupFormI, TxtFieldI, TI] =
      this.c4(TitleStdMainMenu, VisitorLookupForm, TxtField, T);

    return <div>
      <TitleStdMainMenuI t$title="Kasier service"/>
      <SecCon css={bulma.content}>
        <p>
          <TI m="Your duty is to exchange cash for tokens and backwards. "/>
          <TI m="Ask visitor for his ID, assigned him at registration and lookup his token requests. "/>
        </p>
        <VisitorLookupFormI origin={{uid: 0}}
                           t$next="Search requests"
                           next={this.onSubmit}>
          <div class={bulma.field}>
            <TxtFieldI name="uid" t$lbl="Visitor ID" mit="!e r:^[0-9]+$"/>
          </div>
        </VisitorLookupFormI>
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KasierServe> {
  return regBundle(bundleName, mainContainer, KasierServe);
}
