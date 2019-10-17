import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';

import { TxtField } from 'app/component/field/txt-field';
import { DayTimeField } from 'app/component/field/day-time-field';

import bulma from 'app/style/my-bulma.sass';

export class BasicFestInfoFields extends TransCom<{}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [TxtFieldI, DayTimeFieldI] = this.c2(TxtField, DayTimeField);

    return <div class={bulma.field}>
      <TxtFieldI t$lbl="Festival name" name="name" mit="!e rng:3:120 " />
      <DayTimeFieldI t$lbl="Starts at" a="opensAt" min="today"/>
      <TxtFieldI t$lbl="Admin name" name="userName" mit="!e rng:3:120 " />
    </div>;
  }

  at(): string[] { return []; }
}
