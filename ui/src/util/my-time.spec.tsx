import { isoTime, time2Str } from 'util/my-time';

describe('date-fns', () => {
  describe('isoTime / time2Str', () => {
    it('parse date time in ISO format', () => expect(
      time2Str(isoTime('2014-12-30T23:45:44.777Z')))
      .toBe('2014-12-30T23:45:44.777Z'));
  });
});
