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

class VolunteersCtrl extends TransCom<{fid: Fid}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p) {
    const [TI, TitleStdMainMenuI] = this.c2(T, TitleStdMainMenu);
    return <div>
      <TitleStdMainMenuI t$title="Volunteers"/>
      <section>
        <ul class={bulma.list}>
          <li class={bulma.listItem}>
            <Link href={`/admin/festival/waiters/${p.fid}`}>
              <TI m="List waiters"/>
            </Link>
          </li>
          <li class={bulma.listItem}>
            <Link href={`/admin/festival/cashiers/${p.fid}`}>
              <TI m="List cashiers"/>
            </Link>
          </li>
          <li class={bulma.listItem}>
            <Link href={`/admin/festival/invite-links/${p.fid}`}>
              <TI m="Invite links"/>
            </Link>
          </li>
        </ul>
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<VolunteersCtrl> {
  return regBundle(bundleName, mainContainer, VolunteersCtrl);
}
