import { h } from 'preact';
import { MyCo } from 'component/my-component';
import removeEmptyVals from 'collection/remove-empty-values';
import { jne } from 'collection/join-non-empty';

import 'flatpickr/dist/flatpickr.css';
import Flatpickr from 'flatpickr/dist/flatpickr.js';

export type DayTimePickerMode = 'Day' | 'DayTime';

export interface DayTimePickrP {
  onChng: (d: string) => void;
  fmt: string;
  mode: DayTimePickerMode;
  val: string;
  css?: string;
  min?: string;
  max?: string;
}

export class DayTimePickr extends MyCo<DayTimePickrP, {}> {
  // @ts-ignore
  node: HTMLElement;
  // @ts-ignore
  pickr: Flatpickr;

  dMnt() {
    this.pickr = new Flatpickr(
      this.node,
      removeEmptyVals({
        altInputClass: this.props.css,
        minDate: this.props.min,
        maxDate: this.props.max,
        enableTime: this.props.mode === 'DayTime',
        dateForma: this.props.fmt,
        onChange: (dts, ld) => this.props.onChng(ld)
      }));
  }

  wUmt() {
    this.pickr.destroy();
  }

  render() {
    return <input class={jne(this.props.css)}
                  value={this.props.val}
                  ref={node => this.node = node} />;
  }
}
