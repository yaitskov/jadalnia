import { h } from 'preact';
import { MyCo } from 'component/my-component';

export interface WmP {
  c: () => void;
}

export class Wm extends MyCo<WmP, {}> {
  protected wMnt(): void {
    this.props.c();
  }

  render() {
    return <b/>;
  }
}
