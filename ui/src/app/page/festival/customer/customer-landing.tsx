import { h } from 'preact';
import {route} from 'preact-router';
import { jne } from 'collection/join-non-empty';
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {FestSr} from "app/service/fest-service";
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu2 } from 'app/title-std-main-menu-2';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import {Fid, FestInfo, Announce, Open, Close} from 'app/page/festival/festival-types';

import {SignUpSr} from "app/auth/sign-up-service";
import {RestErrCo} from "component/err/error";
import {Loading} from "component/loading";
import {UserTypeLbl} from "app/page/festival/volunteer/user-type-lbl";
import {Customer} from "app/service/user-types";

import bulma from 'app/style/my-bulma.sass';
import {time2Str, localDateYmd, isoTime, localTimeHm} from 'util/my-time';
import {Thenable} from "async/abortable-promise";
import {uuidV4} from "util/crypto";
import {UserRegReq} from "app/page/sign-up/sign-up-form";

export interface CustomerLandingP {
  fid: Fid;
}

export interface CustomerLandingS extends TransComS {
  fest?: FestInfo;
  e?: Error;
}

class CustomerLanding extends TransCom<CustomerLandingP, CustomerLandingS> {
  // @ts-ignore
  private $festSr: FestSr;
  // @ts-ignore
  private $signUp: SignUpSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
    this.signUpAndShowMenu = this.signUpAndShowMenu.bind(this);
  }

  reg(signUpForm: UserRegReq): Thenable<any> {
    return this.$signUp.signUp(signUpForm).tn(uid => {
      route(`/festival/visitor/menu/${this.pr.fid}`);
    }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  signUpAndShowMenu() {
    this.reg({
      session: uuidV4(),
      festivalId: this.pr.fid,
      userType: Customer,
      name: `visitor ${time2Str(new Date())}`
    });
  }

  wMnt() {
    this.$festSr.getInfo(this.props.fid)
        .tn(fInfo => this.ust(st => ({...st, fest: fInfo, e: null})))
        .ctch(e => this.ust(st => ({...st, e: e})))
  }

  render(p, st) {
    const [TI, TitleStdMainMenu2I, LoadingI, UsrLblI]
    = this.c4(T, TitleStdMainMenu2, Loading, UserTypeLbl);
    return <div>
      <TitleStdMainMenu2I title={<TI m="Welcome to name!" name={!!st.fest && st.fest.name}/>} />
      <SecCon css={bulma.content}>
        {!st.fest && !st.e && <LoadingI/>}
        <RestErrCo e={st.e} />
        {!!st.fest && st.fest.state == Announce && <div>
          <p>
            <TI m="It is good place to eat, drink and enjoy a show. " />
          </p>
          <p>
            <TI m="Festival start is at your time zone"
                on={localDateYmd(isoTime(st.fest.opensAt))}
                at={localTimeHm(isoTime(st.fest.opensAt))} />
          </p>
          <p>
            <TI m="On this site you can order festival meals online "/>
            <TI m="and pick them up when there are ready, "/>
            <TI m="without wasting time in line."/>
          </p>
          <p>
            <TI m="There are following steps: pick meals, put order, and pay order to cashier. " />
            <TI m="After that enjoy the show and check line progress from time to time. "/>
            <TI m="A few minutes before you turn come to serving table " />
            <TI m="and be ready to be called and pick the order." />
          </p>
          <div class={bulma.buttons}>
            <button class={jne(bulma.button, bulma.isPrimary)} onClick={this.signUpAndShowMenu}>
              <TI m="see menu" />
            </button>
          </div>
        </div>
        }
        {!!st.fest && st.fest.state == Open && <div>
          <p>
            <TI m="Welcome to name!" name={st.fest.name}/>
          </p>
          <p>
            <TI m="It is good place to eat, drink and enjoy a show. " />
          </p>
          <p>
            <TI m="The festival is up and running. " />
          </p>
          <p>
            <TI m="On this site you can order festival meals online "/>
            <TI m="and pick them up when there are ready, "/>
            <TI m="without wasting time in line."/>
          </p>
          <p>
            <TI m="There are following steps: pick meals, put order, and pay order to cashier. " />
            <TI m="After that enjoy the show and check line progress from time to time. "/>
            <TI m="A few minutes before you turn come to serving table " />
            <TI m="and be ready to be called and pick the order." />
          </p>
          <div class={bulma.buttons}>
            <button class={jne(bulma.button, bulma.isPrimary)}
                    onClick={this.signUpAndShowMenu}>
              <TI m="see menu" />
            </button>
          </div>
        </div>}
        {!!st.fest && st.fest.state == Close && <div>
          <p>
            <TI m="This festival is over, but it will happen again next year. " />
            <TI m="We hope to see you then." />
          </p>
        </div>}
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CustomerLanding> {
  return regBundleCtx(bundleName, mainContainer, CustomerLanding,
      o => o.bind([['festSr', FestSr]]) as FwdContainer);
}
