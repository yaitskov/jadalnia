import { Component, h } from 'preact';
import { jne } from 'collection/join-non-empty';

export interface IfSt {
  f: boolean;
  css?: string;
}

export class If extends Component<IfSt> {
  render() {
    return <div class={jne(this.props.css)}
                style={{ display: this.props.f ? 'block' : 'none' }}>
      {
        this.props.children // @ts-ignore
      }
    </div>;
  }
}
