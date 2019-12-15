import { h } from 'preact';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import {Fid, FestInfo, Announce, Open, Close} from 'app/page/festival/festival-types';
import {FestSr} from "app/service/fest-service";
import bulma from 'app/style/my-bulma.sass';
import {RestErrCo} from "component/err/error";
import {Loading} from "component/loading";
import {UserTypeLbl} from "app/page/festival/volunteer/user-type-lbl";
import {UserType} from "app/service/user-types";
import { Link } from 'preact-router';

export interface VolunteerLandingP {
  fid: Fid;
  userType: UserType;
  nextPage: string;
}

export interface VolunteerLandingS extends TransComS {
  fest?: FestInfo;
  e?: Error;
}

export class VolunteerLanding extends TransCom<VolunteerLandingP, VolunteerLandingS> {
  // @ts-ignore
  private $festSr: FestSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$festSr.getInfo(this.props.fid)
      .tn(fInfo => this.ust(st => ({...st, fest: fInfo, e: null})))
      .ctch(e => this.ust(st => ({...st, e: e})))
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI, UsrLblI]
      = this.c4(T, TitleStdMainMenu, Loading, UserTypeLbl);
    return <div>
      <TitleStdMainMenuI t$title="Be volunteer!"/>
      <SecCon css={bulma.content}>
        {!st.fest && !st.e && <LoadingI/>}
        <RestErrCo e={st.e} />
        {!!st.fest && st.fest.state == Announce && <div>
          <p>
            <TI m="Welcome to name! We need volunteers' help." name={st.fest.name}/>
          </p>
          <p>
            <TI m="It is a fun and great opportunity to meet new people and make friends."/>
          </p>
          <p>
            <TI m="Here you can register as a utype, but there are could be other duties, so contact manager for more info."
                utype={<UsrLblI userType={p.userType}/>}/>
          </p>
          <p>
            <TI m="Festival start is at" at={st.fest.opensAt}/>
            <TI m="your time zone"/>
          </p>

          <div class={bulma.buttons}>
            <Link class={bulma.button} href={p.nextPage}>
              <TI m="Enlist as utype" utype={<UsrLblI userType={p.userType}/>} />
            </Link>
          </div>
        </div>
        }
        {!!st.fest && st.fest.state == Open && <div>
          <p>
            <TI m="Welcome to name! We need volunteers' help." name={st.fest.name}/>
          </p>

          <p>
            <TI m="The festival is already running."/>
          </p>

          <div class={bulma.buttons}>
            <Link class={bulma.button} href={p.nextPage}>
              <TI m="Enlist as utype" utype={<UsrLblI userType={p.userType}/>} />
            </Link>
          </div>
        </div>}
        {!!st.fest && st.fest.state == Close && <div>
          <p>
            <TI m="The festival is over, but don't worry, check schedule for next year." />
          </p>
        </div>}
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}
