import { h } from 'preact';
import { route } from 'preact-router';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { SecCon } from 'app/component/section-container';
import { FF } from 'app/component/logo-shortcut';
import bulma from 'app/style/my-bulma.sass';
import { jne } from 'collection/join-non-empty';
import {BackBtn} from "component/form/back-button";
import {PushSr} from "worker/push/push-service";
import {RestErrCo} from "component/err/error";

export interface PushPermissionP  {
  nextUrl: string;
}

export interface PushPermissionS extends TransComS {
  supported?: boolean;
  allowed?: boolean;
  gettingPerms: boolean;
  e?: Error;
}

export class PushPermission extends TransCom<PushPermissionP, PushPermissionS> {
  // @ts-ignore
  $pushSr: PushSr;

  constructor(props) {
    super(props);
    this.st = {gettingPerms: false, at: this.at()};
    this.askPerms = this.askPerms.bind(this);
    this.skip = this.skip.bind(this);
  }

  askPerms() {
    this.ust(st => ({...st, gettingPerms: true}));
    this.$pushSr.askForPerms()
      .tn(ok => {
        this.ust(st => ({...st, allowed: ok, gettingPerms: false, e: null}));
      })
      .ctch(e => {
        this.ust(st => ({...st, gettingPerms: false, e: e}));
      });
  }

  skip() {
    route(this.props.nextUrl);
  }

  wMnt() {
    this.ust(st => ({...st,
      supported: this.$pushSr.isSupported()
    }));
    this.$pushSr.isAllowed().tn(canPushP =>
      this.ust(st => ({...st, allowed: canPushP})));
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI] = this.c2(T, TitleStdMainMenu);
    return <div>
      <TitleStdMainMenuI t$title="Allow notification"/>
      <SecCon css={bulma.content}>
        <p>
          <FF/> would like to use the browser notifications feature for user better experience.
        </p>
        <p>
          Having notifications enabled you will be notified about a ready order as quickly
          as with SMS and your phone number is not needed.
        </p>
        <p>
          Without push notifications you need to check the status of your order periodically
          visiting this web site.
        </p>

        {st.supported === undefined && <p>Checking for notification support...</p>}
        {st.supported === false && <p>Your browser doesn't support notifications.</p>}
        {!!st.supported && !st.pushAllowed && <p>
          Click the button below and the browser will ask
          you to enable required permissions.
        </p>}
        {!!st.gettingPerms && <p>Getting permissions...</p>}
        {!!st.allowed && <p>Notifications are enabled, thanks.</p>}

        <RestErrCo e={st.e}/>

        <div class={bulma.buttons}>
          {st.supported && !st.allowed && <button class={jne(bulma.button, bulma.isSuccess)}
                                                  onClick={this.askPerms}>
            <TI m="Request permit for notifications"/>
          </button>}
          {st.allowed && <button class={jne(bulma.button, bulma.isSuccess)} onClick={this.skip}>
            <TI m="Next"/>
          </button>}
          {st.allowed || <button class={jne(bulma.button, bulma.isWarning)} onClick={this.skip}>
            <TI m="Continue without notifications"/>
          </button>}
          <BackBtn/>
        </div>
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}


