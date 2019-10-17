export type Instantiable<T = any> = {new(...args: any[]): T};

export interface Tobj<T> {
  [k: string]: T;
}

export const forM = <T extends {} >(m: Tobj<T>, c: (a: [string, T]) => void): void => Object.entries(m).forEach(c);
export const mapO = <T extends {}, R extends {} >(m: Tobj<T>, f: (a: [string, T]) => R): R[] => Object.entries(m).map(f);

export const keysM = <T extends {} >(m: Tobj<T>): string[] => Object.keys(m);
export const emptyM = <T extends {} >(m: Tobj<T>): boolean => !keysM(m).length;

export const idx = <T extends {} >(e: T, a: T[]): number => a.indexOf(e);
export const aHas = <T extends {} >(e: T, a: T[]): boolean => idx(e, a) >= 0;
export const toMap = <T extends {}, V extends {} >(
  a: T[], keyF: (e: T) => string, valueF: (e: T) => V): Tobj<V> => a.reduce((m, e) => (m[keyF(e)] = valueF(e), m), {});


export const deepEq = <T extends {}>(a: Tobj<T>, b: Tobj<T>): boolean => JSON.stringify(a) === JSON.stringify(b);

export const gNew = <T extends {} >(obj: Tobj<T>,
                                    key: string,
                                    newV: () => T): T => {
                                      let v = obj[key];
                                      if (!v) {
                                        v = obj[key] = newV();
                                      }
                                      return v;
                                    };

export const isAr = (o: any) => Array.isArray(o);
export const isObj = (o: any) => typeof o === 'object' && !!o;
export const isStr = (o: any) => typeof o === 'string';

export const ifStrE = <T extends ({} | void)> (o: any, strF: (s: string) => T, otherF: () => T): T =>
  isStr(o) ? strF(o as string) : otherF();
