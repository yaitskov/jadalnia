import { h } from 'preact';
import { Fid } from 'app/page/festival/festival-types';
import { TransCom, TransComS } from 'i18n/trans-component';
import { T } from 'i18n/translate-tag';
import {CpClickLnk} from "component/copy-click-link";

export class InviteLinksCom extends TransCom<{fid: Fid}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p, st) {
    const TI = this.c(T);
    return <div>
      <p>
        <TI m="Share invite links with volunteers and visitors."/>
        <TI m="Click link to copy." />
      </p>
      <p>
        <CpClickLnk url={`/festival/invite/cashier/${p.fid}`} t$lbl="link for cashier"/>
      </p>
      <p>
        <CpClickLnk url={`/festival/invite/waiter/${p.fid}`} t$lbl="link for waiter"/>
      </p>
      <p>
        <CpClickLnk url={`/festival/invite/customer/${p.fid}`} t$lbl="link for visitor"/>
      </p>
    </div>;
  }

  at(): string[] { return []; }
}

