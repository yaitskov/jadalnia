import { LocalStorage } from 'app/persistence/local-storage';
import { opt } from 'collection/optional';

describe('local-storage-wrapper',
	() => {
		 it('store, clean => no keys', () => {
		   const ls = new LocalStorage();
			 ls.store('hello', 'world');
			 ls.clearAll();
			 expect(ls.allKeys()).toEqual([]);
		 });
		 it('store get match', () => {
		 	 const ls = new LocalStorage();
			 ls.store('hello', 'world');
		   expect(ls.get('hello')).toEqual(opt('world'));
  	});
});
