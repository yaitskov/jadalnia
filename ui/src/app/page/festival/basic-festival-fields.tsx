import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';

import { TxtField } from 'app/component/field/txt-field';
import { DayTimeField } from 'app/component/field/day-time-field';

import bulma from 'app/style/my-bulma.sass';

// import { StartTimeField } from 'app/page/festival/field/start-time-field';

export class BasicFestInfoFields extends TransCom<{}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [TxtFieldI, DayTimeFieldI] = this.c2(TxtField, DayTimeField);
    /* const [FestNameFieldI, StartDateFieldI, StartTimeFieldI]
     * = this.c3(FestNameField, StartDateField, StartTimeField);*/
    /* const [PlacePickerI, TicketPriceFieldI, DescriptionFieldI]

     * = this.c3(PlacePicker, TicketPriceField, DescriptionField);*/
    return <div class={bulma.field}>
      <TxtFieldI t$lbl="Festival name" name="name" mit="!e rng:3:120 " />
      <DayTimeFieldI t$lbl="Starts at" a="startAt" min="today"/>
    </div>;

    {/* <PlacePickerI />
        <StartDateFieldI />
        <StartTimeFieldI />
        <TicketPriceFieldI />
        <DescriptionFieldI /> */}
  }

  at(): string[] { return []; }
}
