import { h } from 'preact';
import { time2Str } from 'util/my-time';
import { SignUpSr } from 'app/auth/sign-up-service';
import { resolved } from 'async/abortable-promise';
import { Link, route } from 'preact-router';
import { Container } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { BasicFestInfoForm } from 'app/page/festival/basic-festival-info-form';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import { LocalStorage } from 'app/persistence/local-storage';
import { Thenable } from 'async/abortable-promise';
import { Opt, nic, opt } from 'collection/optional';
import { BasicFestInfo, newBasicFestInfo } from 'app/page/festival/basic-festival-info';
import { NewFestival } from 'app/page/festival/festival-types';

import bulma from 'app/style/my-bulma.sass';

export interface NewFestS extends TransComS {
  fest: Opt<NewFestival>;
}

function newFest(): NewFestival {
  return {
    basic: newBasicFestInfo(),
  };
}

class NewFest extends TransCom<{}, NewFestS> {
  // @ts-ignore
  private $locStore: LocalStorage;
  // @ts-ignore
  private $signUp: SignUpSr;

  constructor(props) {
    super(props);
    this.st = {fest: nic(),  at: this.at()};
  }

  wMnt() {
    this.st.fest = opt(this.$locStore.jGet<NewFestival>('newFestival').elf(newFest));
  }

  goToMenu(info: BasicFestInfo): void {
    this.$signUp
        .signUpAdmin(
          {...info,
           opensAt: time2Str(new Date(info.opensAt))})
        .tn(() => {
          this.st.fest.ifV(v => this.$locStore.jStore<NewFestival>(
            'newFestival', {...v, basic: info}));
          route('/festival/new/menu');
        });
  }

  render() {
    const [TI, TitleStdMainMenuI, BasicFestInfoFormI] = this.c3(T, TitleStdMainMenu, BasicFestInfoForm);
    return <div>
      <TitleStdMainMenuI t$title="New Festival"/>
      <SecCon>
        <BasicFestInfoFormI info={this.st.fest.val.basic}
                            onSubmit={info => this.goToMenu(info)} />
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}


export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<NewFest> {
  return regBundleCtx(bundleName, mainContainer, NewFest, (o) => o);
}
