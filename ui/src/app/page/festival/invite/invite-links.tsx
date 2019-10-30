import { h } from 'preact';
import { Container } from 'injection/inject-1k';
import { regBundle } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { Fid } from 'app/page/festival/festival-types';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import bulma from "app/style/my-bulma.sass";
import {jne} from "collection/join-non-empty";
import {InviteLinksCom} from "app/page/festival/invite/invite-links-com";

class InviteLinks extends TransCom<{fid: Fid}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p, st) {
    const [TitleStdMainMenuI, InviteLinksComI]  = this.c2(TitleStdMainMenu, InviteLinksCom);
    return <div>
      <TitleStdMainMenuI t$title="Invite links"/>
      <section class={jne(bulma.section, bulma.content)}>
        <InviteLinksComI fid={p.fid}/>
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<InviteLinks> {
  return regBundle(bundleName, mainContainer, InviteLinks);
}
