import { isoTime, time2Str, roundToMinutes, localDateYmd } from 'util/my-time';

describe('date-fns', () => {
  describe('isoTime / time2Str', () => {
    it('parse date time in ISO format', () => expect(
      time2Str(isoTime('2014-12-30T23:45:44.777Z')))
      .toBe('2014-12-30T23:45:44.777Z'));
  });
});

describe('my-time', () => {
  describe('roundToMinutes', () => {
    it('cuts', () => expect(roundToMinutes('2014-12-30T23:45:44.777Z')).toBe('2014-12-30 23:45'));
  });
  describe('localDateYmd', () => {
    it('formats yyyy-mm-dd', () => expect(localDateYmd(new Date())).toMatch(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/));
  });
});
