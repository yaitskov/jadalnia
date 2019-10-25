import { h } from 'preact';
import { Container, FwdContainer } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { Fid, FestState } from 'app/page/festival/festival-types';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import { FestSr } from 'app/service/fest-service';
import { jne } from 'collection/join-non-empty';
import { RestErrCo } from "component/err/error";

import bulma from 'app/style/my-bulma.sass';
import { SecCon } from 'app/component/section-container';

export interface FestStateCtrlS extends TransComS {
  state?: FestState;
  e?: Error;
}

class FestStateCtrl extends TransCom<{fid: Fid}, FestStateCtrlS> {
  // @ts-ignore
  $festSr: FestSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
    this.openFest = this.openFest.bind(this);
    this.reopenFest = this.reopenFest.bind(this);
    this.closeFest = this.closeFest.bind(this);
  }

  protected wMnt() {
    this.loadState();
  }

  loadState() {
    this.$festSr.getState(this.props.fid)
      .tn(newFestSt => this.ust(st => ({...st, state: newFestSt})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  setFestState(targetSt: FestState) {
    this.$festSr.setState(this.props.fid, targetSt)
      .tn(ok => {
        if (ok) {
          this.ust(st => ({...st, e: null, state: targetSt}));
        } else {
          this.ust(st => ({...st, e: Error("Fest state was not updated")}));
        }
      }).ctch(e => this.ust(st => ({...st, e: e})));
  }

  reopenFest() {
    this.setFestState('Open');
  }

  openFest() {
    this.setFestState('Open');
  }

  closeFest() {
    this.setFestState('Close');
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI] =
      this.c2(T, TitleStdMainMenu);
    return <div>
      <TitleStdMainMenuI t$title="Fest State"/>
      <SecCon>
        <RestErrCo e={st.e}/>
        {!!st.state || !!st.e || <div>loading...</div>}
        {st.state == 'Open' && <div>
          <p>Festival now is open. Visitors can put orders.</p>
          <div class={jne(bulma.buttons, bulma.isCentered)}>
            <button class={jne(bulma.button, bulma.isWarning)}
                    onClick={this.closeFest}>
              <TI m='Close'/>
            </button>
          </div>
        </div>
        }
        {st.state == 'Close' && <div>
          <p>Festival now is closed. New orders are not accepted.</p>
          <div class={jne(bulma.buttons, bulma.isCentered)}>
            <button class={jne(bulma.button, bulma.isPrimary)}
                    onClick={this.reopenFest}>
              <TI m='Reopen'/>
            </button>
          </div>
        </div>
        }
        {st.state == 'Announce' && <div>
          <p>Festival is not open yet.
            Press Open button to allow accepting orders.
          </p>
          <div class={jne(bulma.buttons, bulma.isCentered)}>
            <button class={jne(bulma.button, bulma.isPrimary)}
                    onClick={this.openFest}>
              <TI m="Open"/>
            </button>
          </div>
        </div>
        }
      </SecCon>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<FestStateCtrl> {
  return regBundleCtx(bundleName, mainContainer, FestStateCtrl,
    o => o.bind([['festSr', FestSr]]) as FwdContainer);
}

