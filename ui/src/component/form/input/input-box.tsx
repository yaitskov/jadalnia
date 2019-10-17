import { h } from 'preact';
import { MyCo } from 'component/my-component';
import { Wm } from 'component/will-mount';
import { Valiform, FormLevel } from 'component/form/validation/form-validation';

import bulma from 'bulma/bulma.sass';

/**
   draw scope for input listeners
 */
export class InputBox extends MyCo<{}, {}> {
  // @ts-ignore
  $valiform: Valiform;
  // @ts-ignore
  form: FormLevel;

  // ensure that previous box is complete
  protected wMnt(): void {
    this.form = this.$valiform.topForm();
    this.form.noListeners();
  }

  // complete box
  private flush(): void {
    this.form.flush();
  }

  render() {
    return <div class={bulma.field}>
      {
        this.props.children // @ts-ignore
      }
      <Wm c={() => this.flush()}/>
    </div>; //[0];
  }
}
