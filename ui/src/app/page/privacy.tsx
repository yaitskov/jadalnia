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
        <h1 class={bulma.title}>Cloud-Sport Privacy Policy</h1>

        <Par>
          Cloud-Sport (CS) collects, stores and uses all information about its users,
          which is provided by users explicitly (filing web forms),
          or implicitly. This information is used to please users as much as possible.
          Though keep in mind that accidental data leaks
          are possible, due bugs in software or its configuration,
          which could be exploited by evil hackers or rivals.
          In that case Cloud-Sport cannot give any warranty how user data could be used.
        </Par>

        <Par>
          Most of user data on Cloud-Sport is publicly available like fullname,
          history of competitions in sport tournaments including outcomes of particular matches.
        </Par>

        <Par>
          Cloud-Sport is integrated with 3rd party systems like
          Speech Recognition from <a href="https://google.com">Google</a> and {' '}
          <a href="https://facebook.com">Facebook</a> social network.
          Those systems could receive user data or provide them to Cloud-Sport.
        </Par>

        <Par>
          A Cloud-Sport user could <b>NOT</b> remove his/her account from
          the system, if the user participated in a tournament, because of
          tournament logical consistency limitation, but the user could
          request to anonymize the account, by
          filing <a href="https://github.com/yaitskov/ping-pong/issues">an
          issue</a>.
        </Par>

        <Par>
          An anonymized account has a random name, erased contact
          information and all sessions. Login is disabled for an anonymized
          account. Tournaments owned by an anonymized account are deleted.
          An account before being anonymized walks over all running
          tournaments, whose user participates in.
        </Par>

        <Par>
          Information about complete tournaments could be archived or removed
          automatically, if nobody is interested in a tournament during long
          time.
        </Par>

        <Par>
          The policy could be changed any time without notification.
        </Par>

        <Par>
          The policy is issued on May 20th 2018.
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
