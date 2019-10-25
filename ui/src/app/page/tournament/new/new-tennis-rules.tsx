import { h } from 'preact';
import { InjSubCom } from 'injection/inject-sub-components';
import { Container } from 'injection/inject-1k';
import { regBundle } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { LocalStorage } from 'app/persistence/local-storage';
import { NewTournament, TennisTournamentRules } from 'app/page/tournament/tournament-types';

class NewTennisRules extends InjSubCom<{}, {}> {
  // @ts-ignore
  private $locStore: LocalStorage;

  render() {
    return this.$locStore.jGet<NewTournament<TennisTournamentRules> >('newTournament')
      .map(newTour =>
        <div>
          <p>new tennis rules stub</p>
          <table>
            <tr>
              <td>name:</td>
              <td>{newTour.basic.name}</td>
            </tr>
            <tr>
              <td>starts at:</td>
              <td>{newTour.basic.startAt}</td>
            </tr>
          </table>
        </div>)
      .elf(() => <div> no basic info about tournament </div>);
  }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<NewTennisRules> {
  return regBundle(bundleName, mainContainer, NewTennisRules);
}
