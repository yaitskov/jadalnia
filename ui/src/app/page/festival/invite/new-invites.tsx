import { h } from 'preact';
import { Link } from 'preact-router';
import { Container } from 'injection/inject-1k';
import { regBundle } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { Fid } from 'app/page/festival/festival-types';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import bulma from "app/style/my-bulma.sass";
import { T } from 'i18n/translate-tag';
import { jne } from 'collection/join-non-empty';
import {InviteLinksCom} from "app/page/festival/invite/invite-links-com";

class NewInvites extends TransCom<{fid: Fid}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, InviteLinksComI] = this.c3(T, TitleStdMainMenu, InviteLinksCom);
    return <div>
      <TitleStdMainMenuI t$title="Volunteer invites"/>
      <section class={jne(bulma.section, bulma.content)}>
        <InviteLinksComI fid={p.fid}/>
        <div class={bulma.buttons + ' ' + bulma['is-centered']}>
          <Link class={bulma.button + ' ' + bulma['is-primary']}
                href={`/admin/festival/control/${p.fid}`}>
            <TI m="To festival control panel"/>
          </Link>
        </div>
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<NewInvites> {
  return regBundle(bundleName, mainContainer, NewInvites);
}
