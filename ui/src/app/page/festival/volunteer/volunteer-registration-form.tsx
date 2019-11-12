import { h } from 'preact';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import {Fid} from 'app/page/festival/festival-types';

import bulma from 'app/style/my-bulma.sass';
import {RestErrCo} from "component/err/error";
import {UserType} from "app/service/user-types";
import {Thenable} from "async/abortable-promise";
import {SignUpSr} from "app/auth/sign-up-service";
import {TxtField} from "app/component/field/txt-field";
import {NextCancelForm} from "app/component/next-cancel-form";
import {UserRegReq} from "app/page/sign-up/sign-up-form";
import {uuidV4} from "util/crypto";
import { route } from 'preact-router';

export type Url = string;

export interface VolunteerRegistrationFormP {
  fid: Fid;
  userType: UserType;
  nextPage: Url;
}

export interface VolunteerRegistrationFormS extends TransComS {
  e?: Error;
  formData: UserRegReq;
}

export class VolunteerRegistrationForm
  extends TransCom<VolunteerRegistrationFormP, VolunteerRegistrationFormS> {
  // @ts-ignore
  private $signUp: SignUpSr;

  constructor(props) {
    super(props);
    this.reg = this.reg.bind(this);
    this.st = {
      formData: {
        session: uuidV4(),
        name: '',
        festivalId: props.fid,
        userType: props.userType
      },
      at: this.at()
    };
  }

  reg(signUpForm: UserRegReq): Thenable<any> {
    return this.$signUp.signUp(signUpForm).tn(uid => {
      route(this.props.nextPage);
    }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    class NextCancelFormT extends NextCancelForm<UserRegReq> {}
    const [TitleStdMainMenuI, TxtFieldI, NextCancelFormTI]
      = this.c3(TitleStdMainMenu, TxtField, NextCancelFormT);
    return <div>
      <TitleStdMainMenuI t$title="Volunteer sign-up"/>
      <SecCon css={bulma.content}>
        <RestErrCo e={st.e} />
        <NextCancelFormTI t$next="Sign-up"
                          origin={st.formData}
                          next={this.reg}>
          <TxtFieldI t$lbl="Your name" name="name" mit="!e rng:3:40 " />
        </NextCancelFormTI>
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}
