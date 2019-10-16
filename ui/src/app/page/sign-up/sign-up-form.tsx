import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';
import { Sform } from 'component/form/sform';
import { Submit } from 'component/form/submit';
import { FullNameField } from 'app/page/sign-up/full-name-field';
import { EmailField } from 'app/page/sign-up/email-field';
import { PhoneField } from 'app/page/sign-up/phone-field';
import { Thenable } from 'async/abortable-promise';

export interface UserRegReq {
  fullName: string;
  email: string;
  phone: string;
}

export interface SignUpFormP {
  onSubmit: (d: UserRegReq) => Thenable<any>;
  regReq: UserRegReq;
}

export class SignUpForm extends TransCom<SignUpFormP, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [SformI, SubmitI, FullNameFieldI] = this.c3(Sform, Submit, FullNameField);
    const [EmailFieldI, PhoneFieldI] = this.c2(EmailField, PhoneField);
    return <SformI data={this.props.regReq}
                   onSend={e => this.props.onSubmit(this.props.regReq)}>
      <FullNameFieldI/>
      <EmailFieldI/>
      <PhoneFieldI/>
      <SubmitI t$text="do sign-up" />
    </SformI>;
  }

  protected at(): string[] { return []; }
}
