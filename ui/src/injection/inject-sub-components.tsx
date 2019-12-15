import { Component } from 'preact';
import { Instantiable } from 'collection/typed-object';
import { MyCo } from 'component/my-component';
import { Container, inject } from 'injection/inject-1k';

export abstract class InjSubCom<P, S> extends MyCo<P, S> {
  protected comps: Map<any, any>  = new Map<any, any>();
  // @ts-ignore TS2564
  protected $container: Container;

  protected c2<A extends Component, B extends Component>(a: Instantiable<A>, b: Instantiable<B>): [Instantiable<A>, Instantiable<B>] {
    return [this.c(a), this.c(b)];
  }

  protected c3<A extends Component, B extends Component, C extends Component>(
    a: Instantiable<A>,
    b: Instantiable<B>,
    c: Instantiable<C>): [Instantiable<A>, Instantiable<B>, Instantiable<C>] {
      return [this.c(a), this.c(b), this.c(c)];
    }

  protected c4<A extends Component, B extends Component, C extends Component, D extends Component>(
    a: Instantiable<A>,
    b: Instantiable<B>,
    c: Instantiable<C>,
    d: Instantiable<D>): [Instantiable<A>, Instantiable<B>, Instantiable<C>, Instantiable<D>] {
      return [this.c(a), this.c(b), this.c(c), this.c(d)];
    }

  protected c<T extends Component>(t: Instantiable<T>): Instantiable<T> {
    let com = this.comps.get(t);
    if (!com) {
      this.comps.set(t, com = inject(t, this.$container));
    }
    return com;
  }
}
