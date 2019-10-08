import { resolved, tJoin, tFold, Thenable, AbrPro } from './abortable-promise';
import { asyncIt } from 'util/test-utils';

describe('promise', () => {
  describe('AbrPro', () => {
    describe('tnr', () => {
      asyncIt('keeps returned value', resolved(3).tnr(_3 => 0), c => c.toBe(3));
      const arr: any[] = [];
      asyncIt('gets returned value', resolved(arr).tnr(a => a.push(a)), c => c.toBe(arr[0]));
    });
    describe('tn', () => {
      it('chain abort', () => {
        const abortCalls: number[] = [];
        new AbrPro<number>(new Promise<number>((ok, op) => {}),
                           {abort: () => abortCalls.push(1)})
          .tn(n => n + 1)
          .tn(n => n * 2)
          .abort();
        expect(abortCalls).toEqual([1]);
      });
      asyncIt('update returned value', resolved(3).tn(_3 => 2), c => c.toBe(2));
      const arr: any[] = [];
      asyncIt('gets returned value', resolved(arr).tn(a => { a.push(a); return a;}), c => c.toBe(arr[0]));
    });
  });

  describe('resolved', () =>
    asyncIt('resolved returns fulfilled promise', resolved(7), c => c.toBe(7)));

  describe('tJoin', () => {
    asyncIt('merges fulfilled promises', tJoin([resolved(1), resolved(2)]), c => c.toEqual([1,2]));
  });

  describe('tFold', () => {
    asyncIt('merges till first non empty',
            tFold([() => resolved([]), () => resolved([1,2]), () => resolved([3, 4])]),
            c => c.toEqual([1, 2]));
    asyncIt('merges empty',
            tFold([() => resolved([]), () => resolved([]), () => resolved([])]),
            c => c.toEqual([]));
    asyncIt('merges first',
            tFold([() => resolved([1]), () => resolved([])]),
            c => c.toEqual([1]));
  });
});
