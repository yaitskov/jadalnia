import { Tobj, Instantiable } from 'collection/typed-object';

export interface Ctx {
}

export interface Bean {
}

function dir(o: any): string[] {
  return Object.getOwnPropertyNames(o);
}

type BeanName = string;
type Scope = 's' | 'p';
type OnInjected<T> = (b: T, c: Container) => T;

type FullBeanDef<T> = [BeanName, Type<T>, Scope, OnInjected<T>];
type BeanDef3<T> = [BeanName, Type<T>, Scope];
type BeanDef2<T> = [BeanName, Type<T>];
type BeanDef<T> = (BeanDef2<T> | BeanDef3<T> | FullBeanDef<T>);

export interface Type<T> extends Function { new (...args: any[]): T; }

export const once : Scope = 's';
export const many : Scope = 'p';

class MetaBean {
  constructor(public clazz: Type<Bean>,
              public scope: Scope,
              public onInjected: (Bean, Container) => Bean) {}
}

export class Container {
  private onceBeans: Tobj<Bean> = {'$container': this};
  private beanFactories: Tobj<MetaBean> = {};

  bind(beanDefs: BeanDef<Bean>[]): Container {
    beanDefs.forEach(beanDef => this.bean(beanDef[0], beanDef[1],
                                          (beanDef[2] as Scope) || once,
                                          (beanDef[3] as OnInjected<Bean>) || ((o, g) => o)));
    return this;
  }

  bean<Bean>(name: string, clazz: Type<Bean>,
          scope: Scope = 's',
          onInjected: OnInjected<Bean> = (o, g) => o): Container {
            name = '$' + name;
            if (this.beanFactories[name]) {
              throw new Error(`factory name [${name}] is busy`);
            }
            this.beanFactories[name] = new MetaBean(clazz, scope, onInjected);
            return this;
          }

  get(name: string): any {
    return this.getByName('$' + name, []);
  }

  public sBean(name: string, obj: Bean): Container {
    this.onceBeans['$' + name] = obj;
    return this;
  }

  public sBeanInj(name: string, obj: Bean): Container {
    this.injectDeps(obj, []);
    return this.sBean(name, obj);
  }

  getByName(name: string, stack: string[]): Bean {
    const b = this.onceBeans[name];
    if (b === undefined) {
      return this.newBean(name, stack);
    }
    return b;
  }

  private newBean(name: string, stack: string[]): Bean {
    if (name === stack[0]) {
      throw Error(`cycle dep ==> ${stack}`);
    }
    stack.push(name);
    const mb = this.beanFactories[name];
    if (mb === undefined) {
      return this.noFactory(name);
    }
    const bean = new mb.clazz();
    if (mb.scope === once) {
      this.onceBeans[name] = bean;
    }
    this.injectDeps(bean, stack);
    return mb.onInjected(bean, this);
  }

  protected noFactory(name): Bean {
    throw new Error(`no factory for bean ${name}`);
  }

  injectDeps<T>(bean: T, stack: string[]): void {
    dir(bean).forEach((n) => {
      if (n[0] === '$') {
        bean[n] = this.getByName(n, stack);
      }
    });
  }
}

export class FwdContainer extends Container {
  constructor(private fwd: Container) {
    super();
  }

  protected noFactory(name): Bean {
    return this.fwd.getByName(name, []);
  }
}

export const inject = <IT extends {}> (T: Instantiable<IT>, container: Container): Instantiable<IT> => (
  class Enhance extends (T as any) {
    constructor(props) {
      super(props);
      container.injectDeps(this, []);
    }
  }) as Instantiable<IT>;
