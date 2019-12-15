import { Tobj } from 'collection/typed-object';

type KeyF<T> = (t: T) => string;

export const grpBy = <T extends {}>(f: KeyF<T>, items: T[]): Tobj<T[]> => items.reduce(
  (m, t) => {
    const k = f(t);
    const l = m[k];
    if (l) {
      l.push(t);
    } else {
      m[k] = [t];
    }
    return m;
  },
  {});
