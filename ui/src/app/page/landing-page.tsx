import { h } from 'preact';
import { Link, route } from 'preact-router';
import { regBundleCtx } from 'injection/bundle';
import { Container } from 'injection/inject-1k';
import { Tobj, Instantiable } from 'collection/typed-object';
import { TransCom, TransComS } from 'i18n/trans-component';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { T } from 'i18n/translate-tag';
import { MyCo } from 'component/my-component';
import { SignUpSr } from 'app/auth/sign-up-service';
import { Footer } from 'app/component/footer';
import { Par } from 'app/component/paragraph';
import { SecCon } from 'app/component/section-container';

import bulma from 'app/style/my-bulma.sass';

export interface LandingPageS extends TransComS {
}

type TileColor = 'red' | 'yellow' | 'green' | 'blue' | 'turquoise';

interface TileP {
  t$title: string;
  t$body: string;
  color: TileColor;
}

const colors: Tobj<string> = {
  red: bulma['is-danger'],
  yellow: bulma['is-warning'],
  green: bulma['is-success'],
  blue: bulma['is-info'],
  turquoise: bulma['is-primary']
};

class Tile extends MyCo<TileP, {}> {
  render() {
    return <div class={bulma.tile + ' ' + bulma.isParent}>
      <article class={bulma.tile + ' ' + bulma.isChild + ' ' + bulma.notification + ' ' + colors[this.props.color]}>
        <p class={bulma.title}>
          {this.props.t$title}
        </p>
        <p class={bulma.content}>
          {this.props.t$body}
        </p>
      </article>
    </div>;
  }
}

class TileLine extends MyCo<{}, {}> {
  render() {
    // @ts-ignore
    return <div class={bulma.tile + ' ' + bulma.isAncestor}>{this.props.children}</div>;
  }
}

class CS extends MyCo<{}, {}> {
  render() {
    return <span>Cloud-Sport</span>;
  }
}

export class LandingPage extends TransCom<{}, LandingPageS> {
  // @ts-ignore
  private $signUp: SignUpSr;

  regAsReferee() {
    this.$signUp.signUpAnonymous().tn(o => route('/tournament/new/choose-sport'));
  }

  constructor(props) {
    super(props);
    this.regAsReferee = this.regAsReferee.bind(this);
    this.st = {at: this.at()};
  }

  render() {
    const [TI, TitleStdMainMenuI, FooterI] = this.c3(T, TitleStdMainMenu, Footer);
    return <div>
      <TitleStdMainMenuI t$title="Welcome to Cloud-Sport"/>
      <SecCon>
        <h1 class={bulma.title}>What is <CS/>?</h1>
        <Par>
          <CS/> is a modern place for conducting sport tournaments in tennis and ping-pong between amateurs.
        </Par>
        <Par>
          Amateur tournaments have their own specific and <CS/> takes that into account.
          Referees have more freedom and control over a tournament flow,
          e.g. there is no problem to add a participant to a group even all its matches are over.
        </Par>
        <Par>
          In short, the slogan of <CS/> is to allow <strong>change rules as a game goes!</strong>
        </Par>
        <Par>
          If you don't know how many people will take a part in the mid of tournament,
          then <CS/> is exactly for you.
        </Par>
      </SecCon>

      <SecCon css={bulma.noTop}>
        <TileLine>
          <Tile t$title="No paper"
                t$body="track a tournament with a phone and save time on printing blanks and rewriting protocols due mistakes"
                color="turquoise" />
          <Tile t$title="No desk"
                t$body="less furniture and more space for games" color="yellow" />
          <Tile t$title="No lines"
                t$body="online registration" color="blue" />
        </TileLine>

        <TileLine>
          <Tile t$title="Enjoy watching"
                t$body="participants submit their own results and referee is out of the loop, but he keeps control and can override match outcome" color="red" />
          <Tile t$title="Facebook integration"
                t$body="publish results in 1 click" color="blue" />
          <Tile t$title="Crowdless"
                t$body="aren't you tired of participants hanging around your desk waiting for their turn and constantly asking boring repetitive questions?! Give them an opportunity to relax in a cafe nearby before the next match starts and don't worry about missing their turn" color="green" />
        </TileLine>

        <TileLine>
          <Tile t$title="Free"
                t$body="amature tournaments are low budget events"
                color="turquoise" />
          <Tile t$title="Flexible"
                t$body="often clubs adhere to some free interpretation of official sport rules, which complicates using traditional ridgid systems, but here you have rule designer and can reflect the unique club culture"
                color="yellow" />
          <Tile t$title="Understandable"
                t$body="group ranking follows drill down approach and explains every decision in ordering rivals."
                color="blue" />
        </TileLine>
      </SecCon>

      <SecCon>
        <div class={bulma.buttons + ' ' + bulma['is-centered']}>
          <button class={bulma.button + ' ' + bulma['is-primary'] + ' ' + bulma['is-large']}
                  onClick={this.regAsReferee}>
            <TI m="Create demo tournament"/>
          </button>
          <Link class={bulma.button + ' ' + bulma['is-large']} href="/features/all">
            <TI m="Detailed feature list"/>
          </Link>
        </div>
      </SecCon>
      <FooterI/>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<LandingPage> {
  return regBundleCtx(bundleName, mainContainer, LandingPage, (o) => o);
}
