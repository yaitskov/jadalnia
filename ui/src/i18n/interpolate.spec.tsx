import { evalTpl } from 'i18n/interpolate';

describe('evalTpl', () => {
  it('literal string as is',
     () => expect(evalTpl('hello', {})).toBe('hello'));
  it('just interpolate parameter',
     () => expect(evalTpl(['hello ', 'n', {}],
                          {n: 'World'})).toBe('hello World'));
  it('interpolate by number 2',
     () => expect(evalTpl(['hello ', 'n', {1: 'world', 2: ['n', ' worlds']}],
                          {n: 2})).toBe('hello 2 worlds'));
  it('interpolate by number 1',
     () => expect(evalTpl(['hello ', 'n', {1: 'world', 2: ['n', ' worlds']}],
                          {n: 1})).toBe('hello world'));

});
