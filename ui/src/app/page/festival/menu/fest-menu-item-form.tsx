import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';
import { NextCancelForm } from 'app/component/next-cancel-form';
import { FestMenuItemFull } from 'app/service/fest-menu-types';
import { TxtField } from "app/component/field/txt-field";

import bulma from 'app/style/my-bulma.sass';
import {Thenable} from "async/abortable-promise";

export interface FestMenuItemFormP {
  menuItem: FestMenuItemFull;
  t$submitLabel: string;
  onSubmit: (fields: FestMenuItemFull) => Thenable<any>;
}

export class FestMenuItemForm extends TransCom<FestMenuItemFormP, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    class NextCancelFormT extends NextCancelForm<FestMenuItemFull> { }
    const [TxtFieldI, NextCancelFormTI] = this.c2(TxtField, NextCancelFormT);

    return <NextCancelFormTI t$next={this.props.t$submitLabel}
                             origin={this.props.menuItem}
                             next={this.props.onSubmit}>
      <div class={bulma.field}>
        <TxtFieldI t$lbl="Dish" name="name" mit="!e rng:3:120" />
        <TxtFieldI t$lbl="Price" name="price" mit="!e r:^[1-9][0-9]*$"/>
        <TxtFieldI t$lbl="Description" name="description" mit="rng:3:2120" />
      </div>
    </NextCancelFormTI>;
  }

  at(): string[] { return []; }
}
