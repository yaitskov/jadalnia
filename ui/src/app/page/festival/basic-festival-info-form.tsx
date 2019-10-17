import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';
import { BasicFestInfoFields } from 'app/page/festival/basic-festival-fields';
import { NextCancelForm } from 'app/component/next-cancel-form';
import { BasicFestInfo } from 'app/page/festival/basic-festival-info';
import {Thenable} from "async/abortable-promise";

export interface BasicFestInfoFormP {
  info: BasicFestInfo;
  onSubmit: (info: BasicFestInfo) => Thenable<any>;
}

export class BasicFestInfoForm extends TransCom<BasicFestInfoFormP, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    class NextCancelFormT extends NextCancelForm<BasicFestInfo> {}
    const [BasicFestInfoFieldsI, NextCancelFormTI] =
      this.c2(BasicFestInfoFields, NextCancelFormT);
    return <NextCancelFormTI t$next="Define menu"
                             origin={this.props.info}
                             next={this.props.onSubmit}>
      <BasicFestInfoFieldsI />
    </NextCancelFormTI>;
  }

  at(): string[] { return []; }
}
