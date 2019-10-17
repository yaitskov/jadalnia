import { Component, h } from 'preact';
import { shallow } from 'preact-render-spy';
import { Container, FwdContainer, inject, many } from './inject-1k';

class Dep {
  public f: string = 'a';
}

class Bean {
  // @ts-ignore TS2564
  private $dep: Dep;
}

class InjectableComponent extends Component<{}, {}> {
  // @ts-ignore TS2564
  private $dep: Dep;

  render() {
    return <div>{this.$dep.f}</div>;
  }
}

describe('1k injection', () => {
  it('inject dependency', () => {
    const bean = new Bean();
    new Container().bind([['dep', Dep]]).injectDeps(bean, []);
    expect(bean['$dep'].f).toBe('a');
  });

  it('default scope is once', () => {
    const beans = [new Bean(), new Bean()];
    const container = new Container().bind([['dep', Dep]]);
    beans.forEach(bean => container.injectDeps(bean, []));
    expect(beans[0]['$dep'] === beans[1]['$dep']).toBe(true);
  });

  it('prototype scope', () => {
    const beans = [new Bean(), new Bean()];
    const container = new Container().bind([['dep', Dep, many]]);
    beans.forEach(bean => container.injectDeps(bean, []));
    expect(beans[0]['$dep'] !== beans[1]['$dep']).toBe(true);
  });


  it('sBean binds existing object', () => {
    const beans = [new Bean(), new Bean()];
    const container = new Container().sBean('dep', new Dep());
    beans.forEach(bean => container.injectDeps(bean, []));
    expect(beans[0]['$dep'] === beans[1]['$dep']).toBe(true);
  });

  it('inject preact component', () => {
    const container = new Container().sBean('dep', new Dep());
    const InjectableComponentI = inject(InjectableComponent, container);
    const dom = shallow(<InjectableComponentI />);
    expect(dom.find('div').text()).toBe('a');
  });

  it('fwd to parent container', () => {
    const beans = [new Bean(), new Bean()];
    const container = new Container().bind([['dep', Dep, many]]);
    beans.forEach(bean => new FwdContainer(container).injectDeps(bean, []));
    expect(beans[0]['$dep'] !== beans[1]['$dep']).toBe(true);
  });
});
