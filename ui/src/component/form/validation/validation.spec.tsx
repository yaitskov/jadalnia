import { asyncIt, isA } from 'util/test-utils';
import { resolved, tJoin, AbrPro } from 'async/abortable-promise';
import { Invalid } from 'component/form/validation/invalid';
import { CustomValidator, InputCheckP } from 'component/form/validation/input-check-def';
import { ValiCache, ValidatorF, Validation, Validator, Max, Min, Match, IntType, NotEmpty, ValiChain } from 'component/form/validation/validation';

function pass(msg: string, validator: Validator, value: string) {
  asyncIt('pass ' + msg, validator.check(value), c => c.toEqual([]));
}

function reject(msg: string, validator: Validator, value: string) {
  asyncIt('reject ' + msg, validator.check(value), c => c.toEqual([isA(Invalid)]));
}

function exprCheck(expr: string): InputCheckP[] {
  return [{on: 'c', mit: expr}];
}

function funcCheck(cv: CustomValidator): InputCheckP[] {
  return [{on: 'c', mit: cv}];
}

function passReject(msg: string, validator: Validator, good: string, bad: string) {
  pass(msg, validator, good);
  reject(msg, validator, bad);
}

type Callback0 = () => void;

describe('validation', () => {
  describe('max', () => {
    pass('eq', new Max(10), '10');
    pass('less', new Max(10), '9');
    reject('more', new Max(10), '11');
  });

  describe('min', () => {
    pass('eq', new Min(10), '10');
    reject('less', new Min(10), '9');
    pass('more', new Min(10), '11');
  });

  describe('int type', () => {
    pass('zero', new IntType(), '0');
    reject('negative', new IntType(), '-9');
    reject('letter', new IntType(), '1l1');
  });

  describe('not empty', () => {
    pass('0', new NotEmpty(), '0');
    pass('letter', new NotEmpty(), 'a');
    reject('empty string', new NotEmpty(), '');
  });

  describe('match', () => {
    pass('exact', new Match(/^aaa$/), 'aaa');
    reject('sub', new Match(/^a$/), 'aaa');
  });

  describe('vali chain', () => {
    pass('both pass', new ValiChain([new Match(/^a$/), new Match(/^.$/)]), 'a');
    pass('empty', new ValiChain([]), 'a');
    reject('last rejects', new ValiChain([new Match(/^a$/), new Match(/^b$/)]), 'a');
    reject('first rejects', new ValiChain([new Match(/^b$/), new Match(/^a$/)]), 'a');
  });

  describe('validation', () => {
    passReject('expr non empty',
               new Validation().build(exprCheck('!e'), 'f'), 'a', '');
    passReject('expr non empty and captial',
               new Validation().build(exprCheck('!e r:^[A-Z]$'), 'f'), 'A', 'a');
    pass('func', new Validation().build(funcCheck((s) => resolved([])), 'f'), '8');
    reject('func', new Validation().build(
      funcCheck((s) => resolved([new Invalid('boo', 'custom', {})])), 'f'), '8');
  });

  describe('validation cache', () => {
    pass('base', new ValiCache(new NotEmpty()), 'a');
    describe('call base if value changed', () => {
      const calls: string[] = [];
      const vCache = new ValiCache(new ValidatorF('n', (s) => {
        calls.push(s); return resolved([]); }));
      asyncIt('no errors', tJoin([vCache.check('a'), vCache.check('b')])
        .tn(r => r.flat())
        .tnr(() => it('2 call', () => expect(calls).toEqual(['a', 'b']))),
              c => c.toEqual([]));
    });
    describe('interrupt progress', () => {
      const abortCalls: string[] = [];
      const vCache = new ValiCache(
        new ValidatorF(
          'n', (s) => new AbrPro<Invalid[]>(
            abortCalls.length > 0 ? Promise.resolve([]) : new Promise<Invalid[]>((ok, bad) => {}),
            {abort: () => abortCalls.push(s)})));
      asyncIt('no errors', tJoin([vCache.check('a'), vCache.check('b')])
        .tn(r => r.flat())
        .tnr(() => it('1 call', () => expect(abortCalls).toEqual(['a']))),
              c => c.toEqual([]));
    });

    describe('wait progress', () => {
      const calls: string[] = [];
      const vCache = new ValiCache(
        new ValidatorF(
          'n', (s) => {
            calls.push(s);
            return new AbrPro<Invalid[]>(
              new Promise<Invalid[]>((ok, bad) => {}),
              {abort: () => {}});
          }));
      it('promise is reused',
         () => expect(vCache.check('a') === vCache.check('a')).toBe(true));
    });

    describe('use cache instead of base', () => {
      const calls: number[] = [];
      const vCache = new ValiCache(new ValidatorF('n', (s) => {
        calls.push(1); return resolved([]); }));
      asyncIt('no errors', tJoin([vCache.check('a'), vCache.check('a')])
        .tn(r => r.flat())
        .tnr(() => it('1 call', () => expect(calls).toEqual([1]))),
              c => c.toEqual([]));
    });
  });
});
