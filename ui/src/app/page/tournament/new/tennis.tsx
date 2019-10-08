import { h } from 'preact';
import { resolved } from 'async/abortable-promise';
import { Link, route } from 'preact-router';
import { Container } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { BasicTourInfoForm } from 'app/page/tournament/basic-tournament-info-form';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import { LocalStorage } from 'app/persistence/local-storage';
import { Thenable } from 'async/abortable-promise';
import { Opt, nic, opt } from 'collection/optional';
import { BasicTourInfo, newBasicTourInfo } from 'app/page/tournament/basic-tournament-info';
import { NewTournament, TennisTournamentRules } from 'app/page/tournament/tournament-types';

import bulma from 'app/style/my-bulma.sass';

export interface NewTennisTourS extends TransComS {
  tour: Opt<NewTournament<TennisTournamentRules>>;
}

function newTennisTour(): NewTournament<TennisTournamentRules> {
  return {
    basic: newBasicTourInfo('TE'),
    rules: {}
  };
}

class NewTennisTour extends TransCom<{}, NewTennisTourS> {
  // @ts-ignore
  private $locStore: LocalStorage;

  constructor(props) {
    super(props);
    this.st = {tour: nic(),  at: this.at()};
  }

  wMnt() {
    this.st.tour = opt(this.$locStore.jGet<NewTournament<TennisTournamentRules> >('newTournament').elf(newTennisTour));
  }

  gotoRules(info: BasicTourInfo): void {
    this.st.tour.ifV(v => this.$locStore.jStore<NewTournament<TennisTournamentRules> >('newTournament', {...v, basic: info}));
    route('/tournament/new/tennis/rules');
  }

  render() {
    const [TI, TitleStdMainMenuI, BasicTourInfoFormI] = this.c3(T, TitleStdMainMenu, BasicTourInfoForm);
    return <div>
      <TitleStdMainMenuI t$title="New Tennis Tournament / Basic"/>
      <SecCon>
        <BasicTourInfoFormI info={this.st.tour.val.basic}
                            onSubmit={info => this.gotoRules(info)} />
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}


export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<NewTennisTour> {
  return regBundleCtx(bundleName, mainContainer, NewTennisTour, (o) => o);
}
