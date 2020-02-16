import { h } from 'preact';

import bulma from 'app/style/my-bulma.sass';

import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Fid, Uid} from 'app/page/festival/festival-types';
import {TokenRequestForApprove, TokenSr} from 'app/service/token-service';
import { TransCom, TransComS } from 'i18n/trans-component';
import { T } from 'i18n/translate-tag';
import { SecCon } from 'app/component/section-container';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import {Loading} from "component/loading";
import { Link } from 'preact-router';
import {jne} from "collection/join-non-empty";
import {BackBtn} from "component/form/back-button";
import { U } from 'util/const';


export interface KasierTokenRequestsP {
  fid: Fid;
  vid: Uid;
}

export interface KasierTokenRequestsS extends TransComS {
  e?: Error;
  approvingInProgress: boolean;
  tokenRequests?: TokenRequestForApprove[];
  tokensForApprove: Boolean[];
  rejectedRequests?: TokenRequestForApprove[];
}

class KasierTokenRequests extends TransCom<KasierTokenRequestsP, KasierTokenRequestsS> {
  // @ts-ignore
  $tokenSr: TokenSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at(), tokensForApprove: [], approvingInProgress: false};
    this.approveSelectedRequests = this.approveSelectedRequests.bind(this);
  }

  approveSelectedRequests() {
    this.ust(st => ({...st, approvingInProgress: true}));

    this.$tokenSr.approveSelectedRequest({
        customer: this.pr.vid,
        tokens: this.st.tokensForApprove.map(
          (v, idx) => v ? this.st.tokenRequests!![idx].tokenId : -1)
          .filter(x => x >= 0)
      })
      .tn(acceptedRequests => this.ust(st => ({...st,
        approvingInProgress: false,
        tokensForApprove: st.tokenRequests.map(() => false),
        rejectedRequests: this.findRejectedRequests(acceptedRequests)})))
      .ctch(e => this.ust(st => ({...st, e: e,
        approvingInProgress: false,
        rejectedRequests: [...st.tokenRequests]})));
  }

  findRejectedRequests(acceptedRequests: TokenRequestForApprove[]): TokenRequestForApprove[] {
    return this.st.tokenRequests!!.filter((req, idx) => this.st.tokensForApprove[idx] && acceptedRequests.findIndex(
      acReq => acReq.tokenId == req.tokenId) < 0);
  }

  flipRequestSelection(idx: number) {
    this.ust(st => ({...st, tokensForApprove: st.tokensForApprove.map(
      (v, i) => idx === i ? !v : v)}));
  }

  sumSelectedRequests(): number {
    return this.st.tokensForApprove
      .map((f, i) => f ? this.st.tokenRequests!![i].amount : 0)
      .reduce((a, b) => a  + b, 0);
  }

  sumRejectedRequests(): number {
    return this.st.rejectedRequests!!.reduce((s, item) => s + item.amount, 0)
  }

  wMnt() {
    this.$tokenSr.findTokenRequestsByUid(this.pr.vid)
      .tn(requests => this.ust(st => ({...st,
        tokenRequests: requests,
        tokensForApprove: requests.map(() => false)
      })))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI] = this.c3(T, TitleStdMainMenu, Loading);
    return <div>
      <TitleStdMainMenuI t$title="Token Requests &amp; Returns"/>
      <SecCon css={bulma.content}>
        { !st.e && !st.tokenRequests && <LoadingI /> }
        { !st.rejectedRequests && <div class={bulma.content}>
          { !!st.tokenRequests && !st.tokenRequests.length && <p>
            <TI m="Visitor does not have any open request. Check visitor ID."
                id={p.vid}/>
          </p> }
          { !!st.tokenRequests && !!st.tokenRequests.length && <div>
            <p>
              <TI m="Visitor requested following token requests."
                  id={p.vid}/>
            </p>
            <p>
              <TI m="If not enough change ask visitor to file another request with different amount." />
            </p>
            <table class={bulma.table}>
              <tr>
                <td><TI m="Request ID"/></td>
                <td><TI m="Amount"/></td>
              </tr>
              { st.tokenRequests.map((req, idx) => <tr
                onClick={() => this.flipRequestSelection(idx)}>
                <td class={st.tokensForApprove[idx] ? bulma.isPrimary : ''}>{req.tokenId}</td>
                <td class={req.amount < 0 ? bulma.isDanger : bulma.isSuccess}>
                  {req.amount}
                </td>
              </tr>)}
            </table>

            <p>
              <TI m="Click on rows, visitor wants to approve." />
              <TI m="Green rows with positive amount are requests for token purchase. " />
              <TI m="Red rows with negative amount are requests to return token and get cash back. " />
            </p>
          </div>}
        </div>}

        { st.approvingInProgress && <TI m="Approving in progress..."/> }
        { !!st.rejectedRequests && !!st.rejectedRequests.length && <div class={jne(bulma.message, bulma.isWarning)}>
          <div class={bulma.messageHeader}>
            <TI m="Requests are rejected" />
          </div>
          <div class={bulma.messageBody}>
            <p>
              { this.sumRejectedRequests() < 0 && <TI m="So return withhold part of money (x). "
                                                      x={-1 * this.sumRejectedRequests()} />}
              { this.sumRejectedRequests() > 0 && <TI m="So return part of money (x) back to visitor. "
                                                      x={this.sumRejectedRequests()} /> }
            </p>
            <p>
              <TI m="List of rejected requests:"/>
            </p>
            <table>
              <tr>
                <td><TI m="Request ID"/></td>
                <td><TI m="Amount"/></td>
              </tr>
              { st.rejectedRequests.map(req => <tr>
                <td>{req.tokenId}</td>
                <td>{req.amount}</td>
              </tr>) }
            </table>
          </div>
        </div>}

        { !!st.rejectedRequests && !st.rejectedRequests.length && <div class={jne(bulma.message, bulma.isSuccess)}>
            <div class={bulma.messageHeader}>
              <TI m="All token requests are approved." />
            </div>
            <div class={bulma.messageBody}>
              <TI m="You can start serving next person in line." />
            </div>
        </div> }

        <div class={bulma.buttons}>
          { !st.approvingInProgress && (this.sumSelectedRequests() < 0 || this.sumSelectedRequests() > 0) &&
          <button class={jne(bulma.button, this.sumSelectedRequests() < 0  ? bulma.isWarning : bulma.isPrimary)}
                  onClick={this.approveSelectedRequests}>
            <TI m="Approve x tokens" x={this.sumSelectedRequests()}/>
          </button>}
          { st.rejectedRequests === U && <BackBtn/> }
          { st.rejectedRequests !== U && <Link href={`/festival/kasier/serve/${p.fid}`}
                class={jne(bulma.button, bulma.isSuccess)}>
            <TI m="Next person"/>
          </Link>}
        </div>
      </SecCon>
    </div>
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KasierTokenRequests> {
  return regBundleCtx(bundleName, mainContainer, KasierTokenRequests,
      o => o.bind([['tokenSr', TokenSr]]) as FwdContainer);
}
