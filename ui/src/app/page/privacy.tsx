import { h } from 'preact';
import { T } from 'i18n/translate-tag';
import { Container } from 'injection/inject-1k'
import { Instantiable } from 'collection/typed-object';
import { regBundleCtx } from 'injection/bundle';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { Footer } from 'app/component/footer';

import { Par } from 'app/component/paragraph';
import { SecCon } from 'app/component/section-container';
import {APP_NAME} from "app/app-const";

import bulma from 'bulma/bulma.sass';

export class Privacy extends TransCom<{}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [TI, TitleStdMainMenuI, FooterI] = this.c3(T, TitleStdMainMenu, Footer);
    return <div>
      <TitleStdMainMenuI t$title="Privacy policy"/>
      <SecCon>
        <h1 class={bulma.title}>{APP_NAME} Privacy Policy</h1>

        <Par>
          {APP_NAME} collects, stores and uses all information about its users,
          which is provided by users explicitly (filing web forms),
          or implicitly. This information is used to please users as much as possible.
          Though keep in mind that accidental data leaks
          are possible, due bugs in software or its configuration,
          which could be exploited by evil hackers or rivals.
          In that case {APP_NAME} cannot give any warranty how user data could be used.
        </Par>

        <Par>
          Customer orders aon {APP_NAME} are publicly available, but account could be anonymous.
          Volunteer accounts are publicly available and cannot be anonymous.
        </Par>

        <Par>
          {APP_NAME} is integrated with 3rd party systems like
          Speech Recognition from <a href="https://google.com">Google</a> and {' '}
          <a href="https://facebook.com">Facebook</a> social network.
          Those systems could receive user data or provide them to {APP_NAME}.
        </Par>

        <Par>
          A {APP_NAME} customer user is anonymous by default,
          until you bind your account to Facebook or other authentication system.
        </Par>

        <Par>
          Information about complete festivals could be archived or removed
          automatically.
        </Par>

        <Par>
          The policy could be changed any time without notification.
        </Par>

        <Par>
          The policy is issued on October 13th 2019.
        </Par>
      </SecCon>
      <FooterI/>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<Privacy> {
  return regBundleCtx(bundleName, mainContainer, Privacy, (o) => o);
}
