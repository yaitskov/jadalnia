import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';
import { BasicTourInfoFields } from 'app/page/tournament/basic-tournament-fields';
import { NextCancelForm } from 'app/component/next-cancel-form';
import { BasicTourInfo } from 'app/page/tournament/basic-tournament-info';

export interface BasicTourInfoFormP {
  info: BasicTourInfo;
  onSubmit: (info: BasicTourInfo) => void;
}

export class BasicTourInfoForm extends TransCom<BasicTourInfoFormP, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    class NextCancelFormT extends NextCancelForm<BasicTourInfo> { };
    const [BasicTourInfoFieldsI, NextCancelFormTI] = this.c2(BasicTourInfoFields, NextCancelFormT);
    return <NextCancelFormTI t$next="Define rules"
                            origin={this.props.info} next={this.props.onSubmit}>
      <BasicTourInfoFieldsI />
    </NextCancelFormTI>;
  }

  at(): string[] { return []; }
}
