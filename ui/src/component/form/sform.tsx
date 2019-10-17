import { h } from 'preact';
import removeEmptyVals from 'collection/remove-empty-values';
import { MyCo } from 'component/my-component';
import { Wm } from 'component/will-mount';
import { Valiform, FormLevel } from 'component/form/validation/form-validation';
import {Thenable} from "../../async/abortable-promise";

export interface SformP<T> {
  data: T;
  onSend: (d: T) => Thenable<any>;
}

export class Sform<T> extends MyCo<SformP<T>, {}> {
  // @ts-ignore
  $valiform: Valiform;
  // @ts-ignore
  form: FormLevel;

  protected wMnt(): void {
    this.$valiform.newForm();
    this.form = this.$valiform.topForm();
    this.form.setSubmit(this.props.onSend);
  }

  protected dMnt(): void {
    this.form.setValue(removeEmptyVals({...this.props.data}));
  }

  render() {
    // @ts-ignore
    return <div>
      {this.props.children}
      <Wm c={() => this.$valiform.endForm()}/>
    </div>;
  }
}
