import { uuidV4 } from 'util/crypto';

describe('crypto', () => {
  describe('uuidV4', () => {
    it('36 length', () => expect(uuidV4()).toMatch(/^[0-9a-z-]{36}$/));
  });
});
