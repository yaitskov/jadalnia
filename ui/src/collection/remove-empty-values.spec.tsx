import removeEmptyVals from 'collection/remove-empty-values';

describe('removeEmptyValues', () => {
  it('keep all',
     () => expect(removeEmptyVals({a: '0', b: 'a', c: [], o: {}})).toEqual({a: '0', b: 'a', c: [], o: {}}));
  it('remove key with empty string',
     () => expect(removeEmptyVals({emptyString: '', nullVal: null,
                                   undefVal: undefined, zero: 0}) as any).toEqual({}));
});
