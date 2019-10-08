import { Component, h } from 'preact';

export abstract class MyCo<P, S> extends Component<P, S> {
  constructor(props) {
    super(props);
  }

  componentWillMount() {
    this.wMnt();
  }

  componentWillUnmount() {
    this.wUmt();
  }

  componentDidMount() {
    this.dMnt();
  }

  componentDidUpdate(prevProps: P) {
    this.onUp(prevProps);
  }

  protected onUp(prevProps: P): void {
    // relax
  }

  protected set st(newSt: S) {
    this.setState(newSt); //, () => this.forceUpdate());
  }

  protected get st(): S {
    return this.state;
  }

  protected ust(f: (S) => S): S {
    return this.st = f(this.st);
  }

  protected get pr(): P {
    return this.props;
  }

  protected wMnt(): void {
    console.log("default wMnt");
  }

  protected wUmt(): void {
    console.log("default wUmt");
  }

  protected dMnt(): void {
    console.log("default dMnt");
  }
}
