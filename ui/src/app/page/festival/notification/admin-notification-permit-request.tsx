import { h } from 'preact';
import { Container, FwdContainer } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { Fid } from 'app/page/festival/festival-types';
import { TransCom, TransComS } from 'i18n/trans-component';
import {PushPermission} from "worker/push/push-permission";
import {PushSr} from "worker/push/push-service";


class AdminNotificationPermitRequest extends TransCom<{fid: Fid}, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render(p, st) {
    const PushPermissionI = this.c(PushPermission);
    return <PushPermissionI nextUrl={`/festival/new/menu/${p.fid}`}/>;
  }

  at(): string[] { return []; }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<AdminNotificationPermitRequest> {
  return regBundleCtx(bundleName, mainContainer, AdminNotificationPermitRequest,
    o => o.bind([['pushSr', PushSr]]) as FwdContainer);
}

