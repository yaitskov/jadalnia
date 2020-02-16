import { h } from 'preact';
import { Link } from 'preact-router';

import bulma from 'app/style/my-bulma.sass';

import {Container, FwdContainer} from 'injection/inject-1k';
import {regBundleCtx} from 'injection/bundle';
import { Instantiable, deepEq } from 'collection/typed-object';
import {Fid} from 'app/page/festival/festival-types';
import {TransCom, TransComS} from "i18n/trans-component";
import {TitleStdMainMenu} from "app/title-std-main-menu";
import { T } from 'i18n/translate-tag';
import {KasierHistoryRecord, TokenSr} from "app/service/token-service";
import {BackBtn} from "component/form/back-button";
import {Loading} from "component/loading";
import { jne } from 'collection/join-non-empty';
import { RestErrCo } from 'component/err/error';

export interface KasierHistoryP {
  fid: Fid;
  page: number;
}

export interface KasierHistoryS extends TransComS {
  records?: KasierHistoryRecord[];
  e?: Error;
}

class KasierHistory extends TransCom<KasierHistoryP, KasierHistoryS> {
  // @ts-ignore
  private $tokenSr: TokenSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$tokenSr.kasierRequestHistory(this.pr.page)
      .tn(records => this.ust(st => ({...st, records})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  onUp(prevProps: any) {
    if (!deepEq(this.pr as any, prevProps)) {
      this.wMnt()
    }
  }

  render(p, st) {
    const [TitleStdMainMenuI, LoadingI, TI] =
      this.c3(TitleStdMainMenu, Loading, T);

    return <div>
      <TitleStdMainMenuI t$title="Kasier history" />
      <section class={bulma.section}>
        <div class={bulma.content}>
          {!st.e && !st.records && <LoadingI/>}
          <RestErrCo e={st.e} />
          {!!st.records && !st.records.length && <div class={jne(bulma.message, bulma.isInfo)}>
            <div class={bulma.messageHeader}>
              <TI m="There is no records on the page. " />
            </div>
          </div>}
          {!!st.records && !!st.records.length && <table class={bulma.table}>
            <tr>
              <td>
                <TI m="Request" />
              </td>
              <td>
                <TI m="Amount" />
              </td>
            </tr>
            {st.records.map((rec: KasierHistoryRecord) => <tr>
              <td>
                <Link href={`/festival/kasier/request/control/${p.fid}/${rec.tokenId}`}>
                  {rec.tokenId}
                </Link>
              </td>
              <td>{rec.amount}</td>
            </tr>)}
          </table>}
          <div class={bulma.buttons}>
            <BackBtn />
            {!!st.records && !!st.records.length && <Link class={jne(bulma.button, bulma.isPrimary)}
                  href={`/festival/kasier/history/${p.fid}/${+p.page + 1}`}>
              <TI m="Next page" p={+p.page + 1} />
            </Link>}
          </div>
        </div>
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KasierHistory> {
  return regBundleCtx(bundleName, mainContainer, KasierHistory,
    o => o.bind([
      ['tokenSr', TokenSr]
    ]) as FwdContainer);
}
