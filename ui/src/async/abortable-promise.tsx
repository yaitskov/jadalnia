export interface Abortable {
  abort(): void;
}

export interface Thenable<P> extends Abortable {
  p: Promise<P>;
  //tn2<N>(f: (d: P) => Thenable<N>): Thenable<N>;
  tn<N>(f: (d: P) => Thenable<N> | Promise<N> | N): Thenable<N>;
  tnr(f: (d: P) => void): Thenable<P>;
  ctch(f: (e) => void): Thenable<P>;
  // fnl(f: (e) => void): Thenable<P>;
}

export class AbrPro<P> implements Thenable<P> {
  constructor(public p: Promise<P>,
              private pre: Abortable) {}

  public tnr(f: (x: P) => void): Thenable<P> {
    return this.tn<P>((a) => { f(a); return a; })
  }

  // public tn2<N>(f: (x: P) => Thenable<N>): Thenable<N> {
  //   return new AbrPro<N>(this.p.then(f), this);
  // }

  public tn<N>(f: (x: P) => Thenable<N> | Promise<N> | N): Thenable<N> {
    return new AbrPro<N>(this.p.then(f) as Promise<N>, this);
  }

  public ctch(f: (e) => void): Thenable<P> {
    this.p = this.p.catch(f) as Promise<P>;
    return this;
  }

  /* public fnl(f: (e) => void): Thenable<P> {
   *   this.p = this.p.finally(f) as Promise<P>;
   *   return this;
   * }
   */

  public abort(): void {
    this.pre.abort();
  }
}

export function resolved<T>(v: T): Thenable<T> {
  return new AbrPro<T>(Promise.resolve(v), {abort: () => {}});
}

export function tJoin<T>(thenables: Thenable<T>[]): Thenable<T[]> {
  return new AbrPro<T[]>(
    Promise.all(thenables.map(t => t.p)),
    {abort: () => thenables.forEach(t => t.abort())});
}

export type ThenableFactory<T> = () => Thenable<T[]>;

export function tFold<T>(thenables: ThenableFactory<T>[]): Thenable<T[]> {
  const pending: Thenable<T[]>[] = [];
  return new AbrPro<T[]>(
    new Promise<T[]>(
      (ok, bad) => {
        function fld() {
          if (thenables.length) {
            pending[0] = thenables[0]();
            thenables.shift();
            pending[0].tnr(ts => {
              if (ts.length) {
                ok(ts);
              } else {
                fld();
              }
            });
          } else {
            ok([]);
          }
        }
        fld();
      }),
    {
      abort: () => {
        thenables.length = 0;
        pending.forEach(p => p.abort());
      }
    });
}
