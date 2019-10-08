import { MetaInput } from 'component/form/validation/form-validation';
import { U } from 'util/const';
import { Tobj } from 'collection/typed-object';
import { InputIf, ValiFieldLi } from './input-if';

describe('MetaInput', () => {
  describe('showChecks', () => {
    it('non empty fan names',
       () => expect(new MetaInput(
         {} as InputIf, {},
         [ {chkN: () => 'x' } as ValiFieldLi,
           {chkN: () => U } as ValiFieldLi]).shownChecks()).toEqual(['x']));
  });
});
