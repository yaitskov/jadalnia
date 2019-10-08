import { h } from 'preact';
import { Container } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { LanguageCode, LANG_CODES } from 'i18n/i18n-translator';
import { Footer } from 'app/component/footer';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { SecCon } from 'app/component/section-container';
import { ObVar } from 'collection/observable-variable';
import { CountryFlag } from 'app/component/country-flag';
import { TransCom, TransComS } from 'i18n/trans-component';

import bulma from 'app/style/my-bulma.sass';

class PickLanguage extends TransCom<{}, TransComS> {
  // @ts-ignore
  private $curLang: ObVar<LanguageCode>;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  private choose(lang: LanguageCode): void {
    this.$curLang.val = lang;
  }

  render() {
    const [TI, TitleStdMainMenuI, FooterI] = this.c3(T, TitleStdMainMenu, Footer);
    return <div>
      <TitleStdMainMenuI t$title="Pick language"/>
      <SecCon>
        <h1 class={bulma.title}>
          <TI m="Pick language for user interface"/>
        </h1>
        <ul class={bulma.list}>
          {LANG_CODES.map(
             lang => <li class={bulma.listItem}>
               <button onClick={this.choose.bind(this, lang)}
                       class={bulma.button + ' ' + (this.$curLang.val === lang ? bulma.isPrimary : bulma.isLight)}>
                 <CountryFlag code={lang} />
                 {' '}
                 <TI m={`${lang} language`} />
               </button>
             </li>)}
        </ul>
      </SecCon>
      <FooterI/>
    </div>
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<PickLanguage> {
  return regBundleCtx(bundleName, mainContainer, PickLanguage, (o) => o);
}
