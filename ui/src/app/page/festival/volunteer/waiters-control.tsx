import { h } from 'preact';
import { Link } from 'preact-router';
import { Container, FwdContainer } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { Fid } from 'app/page/festival/festival-types';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import bulma from "app/style/my-bulma.sass";
import { T } from 'i18n/translate-tag';
import {VolunteerSr, UserInfo} from "app/service/volunteer-service";
import {Kelner} from "app/service/user-types";
import { RestErrCo } from 'component/err/error';

export interface CashiersControlS extends TransComS {
  users?: UserInfo[];
  e?: Error;
}

class WaitersControl extends TransCom<{fid: Fid}, CashiersControlS> {
  // @ts-ignore
  $volunteerSr: VolunteerSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$volunteerSr.listByType(this.props.fid, Kelner)
      .tn(users => this.ust(st => ({...st, users: users})))
      .ctch(e => this.ust(st => ({...st, e: e})))
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI] = this.c2(T, TitleStdMainMenu);
    return <div>
      <TitleStdMainMenuI t$title="Volunteers"/>
      <section class={bulma.section}>
        <RestErrCo e={st.e}/>
        {!st.e && !st.users && <p><TI m="loading..." /></p>}
        {!st.e && st.users && !st.users.length && <p><TI m="no volunteers"/></p>}
        {st.users && !!st.users.length && <ul class={bulma.list}>
          {
            st.users.map(u =>
              <li class={bulma.listItem}>
                <Link href={`/admin/volunteer/${p.fid}/${u.uid}`}>
                  {u.name}
                </Link>
              </li>)
          }
        </ul>}
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<WaitersControl> {
  return regBundleCtx(bundleName, mainContainer, WaitersControl,
      o => o.bind([['volunteerSr', VolunteerSr]]) as FwdContainer);
}
