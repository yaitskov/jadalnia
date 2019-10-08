import { optS, nic } from 'collection/optional';

describe('optional', () => {
  describe('optS', () => {
    it('null => empty', () => expect(optS(null)).toEqual(nic()));
    it('"" => empty', () => expect(optS('')).toEqual(nic()));
    it('"0" => Some("0")', () => expect(optS('0').val).toBe('0'));
  });
});
