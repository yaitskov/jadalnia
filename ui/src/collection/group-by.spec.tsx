import { grpBy } from './group-by';

describe('grpBy', () => {
  it('empty array => empty object', () => expect(grpBy(a => a, [])).toEqual({}));
  it('group even/odd',
     () => expect(grpBy(a => a % 2 == 0 ? 'even' : 'odd', [1,2,3,4]))
       .toEqual({even: [2, 4], odd: [1, 3]}));
});
