import { h } from 'preact';

import { U } from 'util/const';
import {Loading} from "component/loading";
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { T } from 'i18n/translate-tag';
import { TitleStdMainMenu } from 'app/title-std-main-menu';
import { TransCom, TransComS } from 'i18n/trans-component';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";
import bulma from 'app/style/my-bulma.sass';
import {jne} from "collection/join-non-empty";
import {KelnerPerformanceRow, PerformanceSr } from "app/service/performance-service";

export interface KelnerPerformanceP {
  fid: Fid;
}

export interface KelnerPerformanceS extends TransComS {
  e?: Error;
  stats?: KelnerPerformanceRow[];
}

class KelnerPerformance extends TransCom<KelnerPerformanceP, KelnerPerformanceS> {
  // @ts-ignore
  private $performanceSr: PerformanceSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  wMnt() {
    this.$performanceSr.kelnerPerformance(this.pr.fid)
      .tn(rows => this.ust(
        st => ({...st, stats: this.sortedList(rows)})))
      .ctch(e => this.ust(st => ({...st, e: e})));
  }

  sortedList(m: KelnerPerformanceRow[]): KelnerPerformanceRow[] {
    return m.sort((a, b) => b.tokens - a.tokens);
  }

  render(p, st) {
    const [TI, TitleStdMainMenuI, LoadingI]
      = this.c3(T, TitleStdMainMenu, Loading);

    return <div>
      <TitleStdMainMenuI t$title="Kelner performance" />
      <section class={jne(bulma.section, bulma.content)}>
        {st.stats === U  && !st.e && <LoadingI/>}
        <RestErrCo e={st.e} />
        {!!st.stats && !st.stats.length && <div class={jne(bulma.message, bulma.isInfo)}>
          <div class={bulma.messageHeader}>
            <TI m="No kelners"/>
          </div>
        </div>}
        {!!st.stats && !!st.stats.length && <table class={bulma.table}>
          <tr>
            <td><TI m="Kelner"/></td>
            <td><TI m="Orders"/></td>
            <td><TI m="Tokens"/></td>
          </tr>
          {st.stats.map((row: KelnerPerformanceRow) => <tr>
            <td>{row.name}</td>
            <td>{row.orders}</td>
            <td>{row.tokens}</td>
          </tr>)}
        </table>}
      </section>
    </div>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KelnerPerformance> {
  return regBundleCtx(bundleName, mainContainer, KelnerPerformance,
    o => o.bind([
      ['performanceSr', PerformanceSr],
    ]) as FwdContainer);
}
