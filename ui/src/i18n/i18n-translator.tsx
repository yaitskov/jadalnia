import { geT } from 'async/abortable-fetch';
import { Thenable } from 'async/abortable-promise';
import { ObVar } from 'collection/observable-variable';

import { Tobj, emptyM } from 'collection/typed-object';
import { AtomicAsync } from 'async/atomic-async';

type TransPhrase = string;
export type LanguageCode = 'ru' | 'en' | 'pl' | 'de';
type TranslatedMessage = string;

export const LANG_CODES: LanguageCode[] = ['ru', 'en', 'pl', 'de'];

export class I18Trans {
  // @ts-ignore
  private $curLang: ObVar<LanguageCode>;

  // @ts-ignore
  private $bundleName: string;

  private dict: AtomicAsync<Tobj<TransPhrase>> = new AtomicAsync(
    () => ({lang: this.$curLang.val}),
    p => geT(`/translation/${this.$bundleName}/${p.lang}.json`)
      .tn(r => r.json()));

  public map(msgId: string): Thenable<TranslatedMessage> {
    return this.dict.val().tn(dic => dic[msgId] || msgId);
  }

  public bulkMap(msgIds: string[]): Thenable<TranslatedMessage[]> {
    return this.dict.val().tn(dic => msgIds.map(msgId => dic[msgId] || msgId));
  }
}
