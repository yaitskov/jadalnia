import { h } from 'preact';
import { Container } from 'injection/inject-1k';
import { regBundle } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import { Link } from 'preact-router';


class AdminCtrlMenu extends TransCom<{}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p) {
    const [TI, TitleStdMainMenuI] =
      this.c2(T, TitleStdMainMenu);
    return <div>
      <TitleStdMainMenuI t$title="Fest Control"/>
      <SecCon>
        <ul>
          <li>
            <Link href={`/admin/festival/volunteers/${p.fid}`}>
              <TI m="volunteers" />
            </Link>
          </li>
          <li>
            <Link href={`/admin/festival/line/stats/${p.fid}`}>
              <TI m="line stats" />
            </Link>
          </li>
          <li>
            <Link href={`/admin/festival/state/control/${p.fid}`}>
              <TI m="open/close festival" />
            </Link>
          </li>
        </ul>
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<AdminCtrlMenu> {
  return regBundle(bundleName, mainContainer, AdminCtrlMenu);
}
