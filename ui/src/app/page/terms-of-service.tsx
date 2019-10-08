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

export interface TermsS extends TransComS {
}

export class Terms extends TransCom<{}, TermsS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [TI, TitleStdMainMenuI, FooterI] = this.c3(T, TitleStdMainMenu, Footer);
    return <div>
      <TitleStdMainMenuI t$title="Terms of service"/>
      <SecCon>
        <h1 class={bulma.title}>Cloud-Sport Terms of Service</h1>

        <Par>
          Last updated: May 20th 2018.
        </Par>

        <Par>
          Please read these Terms of Service ("Terms") carefully before using
          the <a href="https://cloud-sport.org">https://cloud-sport.org</a>.
          The website (the "Service") operated by Daniil Iaitskov ("us", "we", or
          "our").
        </Par>

        <Par>
          Your access to and use of the Service is conditioned on your
          acceptance of and compliance with these Terms. These Terms apply to
          all visitors, users and others who access or use the Service.
        </Par>

        <Par>
          By accessing or using the Service you agree to be bound by these
          Terms. If you disagree with any part of the terms then you may not
          access the Service.
        </Par>

        <h3 class={bulma.title}>Termination</h3>

        <Par>
          We may terminate or suspend access to our Service immediately,
          without prior notice or liability, for any reason whatsoever,
          including without limitation if you breach the Terms.
        </Par>

        <Par>
          All provisions of the Terms which by their nature should survive
          termination shall survive termination, including, without
          limitation, ownership provisions.
        </Par>
      </SecCon>
      <FooterI/>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<Terms> {
  return regBundleCtx(bundleName, mainContainer, Terms, (o) => o);
}
