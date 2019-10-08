import { U } from 'util/const';
import { unreachable } from 'util/test-utils';
import { isObj, forM, mapO, emptyM, keysM, idx, aHas, toMap } from 'collection/typed-object';


describe('typed object:', () => {
  describe('forM', () => {
    it('empty object', () => forM({}, unreachable));

    it('1 pair', () => {
      const r: [string, number][] = [];
      forM({a: 2}, (pair) => r.push(pair));
      expect(r).toEqual([['a', 2]]);
    });
  });

  describe('mapO', () => {
    it('empty', () => expect(mapO({}, unreachable)).toEqual([]));
    it('1 pair', () => expect(mapO({a: 2}, (pair) => pair)).toEqual([['a', 2]]));
  });

  describe('keysM', () => {
    it('empty', () => expect(keysM({})).toEqual([]));
    it('1 pair', () => expect(keysM({a: 2})).toEqual(['a']));
  });

  describe('idx', () => {
    it('zero', () => expect(idx(1, [1, 2])).toBe(0));
    it('last', () => expect(idx(2, [1, 2])).toBe(1));
    it('missing', () => expect(idx(3, [1, 2])).toBe(-1));
  });

  describe('isObj', () => {
    it('{} is obj', () => expect(isObj({})).toBe(true));
    it('undefined is not obj', () => expect(isObj(undefined)).toBe(false));
    it('null is not obj', () => expect(isObj(null)).toBe(false));
    it('123 is not obj', () => expect(isObj(123)).toBe(false));
    it('"hello" is not obj', () => expect(isObj("hello")).toBe(false));
  });

  describe('aHas', () => {
    it('array has', () => expect(aHas(1, [1])).toBe(true));
    it('array lacks', () => expect(aHas(1, [2])).toBe(false));
  });

  describe('toMap', () => {
    it('empty', () => expect(toMap([], unreachable, unreachable)).toEqual({}));
    it('1 item', () => expect(toMap([2], e => 'a', e => e)).toEqual({a: 2}));
  });

  describe('emptyM', () => {
    it('empty true', () => expect(emptyM({})).toBe(true));
    it('1 key false', () => expect(emptyM({a: U})).toBe(false));
  });
});
