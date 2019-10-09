import { h } from 'preact';
import { Link, route } from 'preact-router';
import { regBundleCtx } from 'injection/bundle';
import { Container } from 'injection/inject-1k';
import { Tobj, Instantiable } from 'collection/typed-object';
import { TransCom, TransComS } from 'i18n/trans-component';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { T } from 'i18n/translate-tag';
import { MyCo } from 'component/my-component';
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

class FF extends MyCo<{}, {}> {
  render() {
    return <span>FoodFest</span>;
  }
}

export class LandingPage extends TransCom<{}, LandingPageS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [TI, TitleStdMainMenuI, FooterI] = this.c3(T, TitleStdMainMenu, Footer);
    return <div>
      <TitleStdMainMenuI t$title="Welcome to FoodFest"/>
      <SecCon>
        <h1 class={bulma.title}>What is <FF/>?</h1>
        <Par>
          <FF/> is an online queue for serving food on festivals.
        </Par>
        <Par>
          Integrate with <FF/> if festival stuff cannot serve food orders quickly
          and you want to relief visitors from the burden being in line and missing entertainment part.
        </Par>
        <h4 class={bulma.title}>How it works?</h4>
        <Par>
          Festival's administrator creates a festival in the system, defines menu,
          invites volunteers to work as waiters and cashiers.
        </Par>
        <Par>
          A visitor goes by festival link and automaticaly signed up with an anonymous account.
          After that visitor can explore festival's menu without hassle, put and pay order.
          Visitor can buys tokens in advance.
        </Par>
        <Par>
          Cashier accepts cash and approves electronic tokens,
          which could be used by visitors for payments on the festival.
        </Par>
        <Par>
          Waiter picks order from line, prepares it and marks it as complete.
          Visitor is notified about his ready order in a web browser.
        </Par>
      </SecCon>

      <SecCon css={bulma.noTop}>
        <TileLine>
          <Tile t$title="No paper"
                t$body="No need to print tokens"
                color="turquoise" />
          <Tile t$title="Scaleble order accepting"
                t$body="Visitor puts order on their own" color="yellow" />
          <Tile t$title="Lineless"
                t$body="No live line. Visitors enjoy action." color="blue" />
        </TileLine>

        <TileLine>
          <Tile t$title="Competionary environment"
                t$body="Performance for every waiter is observable" color="red" />
          <Tile t$title="Observable demand"
                t$body="Be aware of needs of all waiting visitor" color="blue" />
          <Tile t$title="Dialog with visitor"
                t$body="An alternative way for feedback and notification about your next festival"
                color="turquoise" />
        </TileLine>

      </SecCon>

      <SecCon>
        <div class={bulma.buttons + ' ' + bulma['is-centered']}>
          <Link class={bulma.button + ' ' + bulma['is-primary'] + ' ' + bulma['is-large']}
                href="/festival/new/start">
            <TI m="Create festival"/>
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
