import { h } from 'preact';
import { Valiform } from 'component/form/validation/form-validation';
import { MyCo } from 'component/my-component';
import { InputCheckP } from 'component/form/validation/input-check-def';

export class InputCheck extends MyCo<InputCheckP, {}> {
  // @ts-ignore TS2564
  private $valiform: Valiform;

  protected wMnt(): void {
    this.$valiform.topForm().check(this.props);
  }

  render() {
    // @ts-ignore TS2564
    return this.props.children[0];
  }
}
