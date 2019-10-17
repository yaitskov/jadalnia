import { h } from 'preact';
import { T } from 'i18n/translate-tag';

import { Container } from 'injection/inject-1k'
import { Instantiable } from 'collection/typed-object';
import { regBundleCtx } from 'injection/bundle';

import { Thenable } from 'async/abortable-promise';
import { SignUpForm, UserRegReq } from 'app/page/sign-up/sign-up-form';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';

import { Footer } from 'app/component/footer';

import bulma from 'bulma/bulma.sass';

interface SignUpP {
}

interface SignUpS extends TransComS {
  regReq: UserRegReq;
}

export class SignUpCom extends TransCom<SignUpP, SignUpS> {
  // @ts-ignore
  private $signUp: SignUpSr;

  constructor(props) {
    super(props);
    this.st = {
      regReq: {
        fullName: '',
        phone: '',
        email: ''
      },
      at: this.at()
    };
  }

  submitHandler(regReq: UserRegReq): Thenable<Response> {
    return this.$signUp.signUp(regReq).tnr(o => window.history.go(-1));
  }

  render() {
    const [TI, SignUpFormI, TitleStdMainMenuI, FooterI]  = this.c4(T, SignUpForm, TitleStdMainMenu, Footer);
    return <div>
      <TitleStdMainMenuI t$title="Sign-Up"/>
      <section class="section">
        <div class={bulma.container}>
          <SignUpFormI regReq={this.st.regReq}
                       onSubmit={rr => this.submitHandler(rr)}/>
        </div>
      </section>
      <FooterI/>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<SignUpCom> {
  return regBundleCtx(bundleName, mainContainer, SignUpCom, (o) => o);
}
