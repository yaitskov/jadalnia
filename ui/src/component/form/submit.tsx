import { h } from 'preact';
import { opt } from 'collection/optional';
import { MyCo } from 'component/my-component';
import { Valiform, FormLevel } from 'component/form/validation/form-validation';

import bulma from 'app/style/my-bulma.sass';

export interface SubmitP {
  t$text: string;
  css?: string;
}

export class Submit extends MyCo<SubmitP, {}> {
  // @ts-ignore
  $valiform: Valiform;
  // @ts-ignore
  form: FormLevel;

  constructor(props) {
    super(props);
    this.onClick = this.onClick.bind(this);
  }

  protected wMnt(): void {
    this.form = this.$valiform.topForm();
  }

  onClick(e) {
    this.form.trySubmit(e);
  }

  render() {
    return <input class={bulma.button + ' ' + bulma.isLink + ' ' + opt(this.props.css as string).el('')}
                  type="submit"
                  value={this.props.t$text} onClick={this.onClick} />;
  }
}
