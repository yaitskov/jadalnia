import { jne } from './join-non-empty';

describe('join non empty', () => {
  it('just null empty string', () => expect(jne(null)).toBe(''));
  it('just undef empty string', () => expect(jne(undefined)).toBe(''));
  it('empty string just empty string', () => expect(jne('')).toBe(''));
  it('no args empty string', () => expect(jne()).toBe(''));
  it('1 args as is', () => expect(jne('a')).toBe('a'));
  it('2 args concat with a space', () => expect(jne('a', 'b')).toBe('a b'));
  it('skip null', () => expect(jne('a', null, 'b')).toBe('a b'));
  it('skip undef', () => expect(jne('a', undefined, 'b')).toBe('a b'));
  it('skip empty string', () => expect(jne('a', '', 'b')).toBe('a b'));
});
