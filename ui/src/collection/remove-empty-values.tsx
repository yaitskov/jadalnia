import { Tobj, keysM, isObj } from 'collection/typed-object';

export default function removeEmptyVals<T>(m: T): T {
  keysM(m as any).forEach(k => {
    if (isObj(m[k] as any)) {
      removeEmptyVals(m[k]);
    } else if (!m[k]) {
      delete m[k];
    }
  });
  return m;
}
