import { h } from 'preact';
import { T } from 'i18n/translate-tag';
import { Container } from 'injection/inject-1k'
import { Instantiable } from 'collection/typed-object';
import { regBundleCtx } from 'injection/bundle';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { Footer } from 'app/component/footer';

import { SecCon } from 'app/component/section-container';

import bulma from 'app/style/my-bulma.sass';

export class Features extends TransCom<{}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [TI, TitleStdMainMenuI, FooterI] = this.c3(T, TitleStdMainMenu, Footer);
    return <div>
      <TitleStdMainMenuI t$title="Feature list"/>
      <SecCon>
        <h1 class={bulma.title}><TI m="Cloud-Sport supports" /></h1>

        <h4 class={bulma.title}><TI m="Scenarios" /></h4>
        <ul class={bulma.list}>
          <li class={bulma.listItem}>
            <TI m="1 big group without play-off" />
          </li>
          <li class={bulma.listItem}>
            <TI m="just play-off ladder up to 1 or 2 defeats" />
          </li>
          <li class={bulma.listItem}>
            <TI m="play in groups and then play-off" />
          </li>
          <li class={bulma.listItem}>
            <TI m="match for 3rd place" />
          </li>
          <li class={bulma.listItem}>
            <TI m="console tournaments" />
            <ul class={bulma.list}>
              <li class={bulma.listItem}>
                <TI m="for group loosers" />
              </li>
              <li class={bulma.listItem}>
                <TI m="for play-off loosers" />
              </li>
            </ul>
          </li>
        </ul>

        <h4 class={bulma.title}><TI m="Kinds of Sport" /></h4>
        <ul class={bulma.list}>
          <li class={bulma.listItem}>
            <TI m="Tennis" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Ping-pong" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Squash" />
          </li>
        </ul>

        <h4 class={bulma.title}><TI m="Scoring options" /></h4>
        <p>
          <TI m="For every tournament you can build your own scoring system out of rules below and use it for all tournaments in the seria:" />
        </p>
        <ul class={bulma.list}>
          <li class={bulma.listItem}>
            <TI m="Count won matches" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Face to face outcome" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Balls balance" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Sets balance" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Won sets" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Lost sets" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Won balls" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Lost balls" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Weighted maches" />
          </li>
          <li class={bulma.listItem}>
            <TI m="ATP rules for sections part d.i, d.ii and d.iii" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Draw - randomly order" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Play short extra match with special rules" />
          </li>
        </ul>

        <p class={bulma.content}>
          Every rule has parameters configuring which matches to take into account: exclude interrupted matches,
          all maches in group or matches just between competing rivals.
        </p>
        <p class={bulma.content}>
          Cloud-Sport expains scoring decision made based on the rules your provided, so it is not going to be black box.
        </p>

        <h4 class={bulma.title}><TI m="Flexibility features" /></h4>
        <ul class={bulma.list}>
          <li class={bulma.listItem}>
            <TI m="Add new participant to a group with complete matches" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Add new group to running tournament" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Move participants between group" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Change group/play off match outcome" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Special rules for play-off" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Participant can walk over any time or referee can expel participant" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Count just sets options" />
          </li>
        </ul>

        <h4 class={bulma.title}><TI m="Technical limits" /></h4>
        <ul class={bulma.list}>
          <li class={bulma.listItem}>
            Max group size is 20 paritcipants
          </li>
          <li class={bulma.listItem}>
            Max number of groups is 128
          </li>
          <li class={bulma.listItem}>
            Max number of participants for play-off ladder is 128
          </li>
        </ul>

        <h4 class={bulma.title}><TI m="Other features" /></h4>
        <ul class={bulma.list}>
          <li class={bulma.listItem}>
            <TI m="Multiple categories in a tournament (for men, women, 50+, etc)" />
          </li>
          <li class={bulma.listItem}>
            <TI m="Image generate for group results ready to be posted on social networks (FB, Twitter)"/>
          </li>
          <li class={bulma.listItem}>
            <TI m="Export/import tournament state into/from json file" />
          </li>
        </ul>
      </SecCon>
      <FooterI/>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<Features> {
  return regBundleCtx(bundleName, mainContainer, Features, (o) => o);
}
