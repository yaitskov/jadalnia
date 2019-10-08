import { Valiform } from 'component/form/validation/form-validation';
import { Validation } from 'component/form/validation/validation';
import { ToDo } from 'app/todo-form';
import { ObList } from 'collection/observable-list';
import { ObVar } from 'collection/observable-variable';
import { I18Trans, LanguageCode } from 'i18n/i18n-translator';
import { UserAuth } from 'app/auth/user-auth';
import { LocalStorage } from 'app/persistence/local-storage';
import { CommonUtil } from 'app/common-util';
import { Container, inject } from 'injection/inject-1k';
import { SignUpSr } from 'app/auth/sign-up-service';

export const container = new Container();

const locStore = new LocalStorage();

container
  .bind(
    [
      ['cutil', CommonUtil],
      ['signUp', SignUpSr],
      ['userAuth', UserAuth],
      ['valiform', Valiform],
      ['validation', Validation],
      ['i18Trans', I18Trans],
    ])
  .sBean('bundlesCtx', {})
  .sBean('bundleName', 'bootstrap')
  .sBean('locStore', locStore)
  .sBean('curLang', new ObVar<LanguageCode>(locStore.get('myLang').el('pl') as LanguageCode)
    .onSet(lang => {
      locStore.store('myLang', lang);
      window.history.go(-1);
    }))
  .sBean('todoList', new ObList<ToDo>());
