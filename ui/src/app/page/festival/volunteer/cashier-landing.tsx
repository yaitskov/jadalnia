import { h } from 'preact';
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Fid} from 'app/page/festival/festival-types';
import {FestSr} from "app/service/fest-service";
import {InjSubCom} from "injection/inject-sub-components";
import {VolunteerLanding} from "app/page/festival/volunteer/volunteer-landing";
import {Kasier, Kelner} from "../../../service/user-types";

class CashierLanding extends InjSubCom<{fid: Fid}, {}> {
  render(p, st) {
    const VolunteerLandingI = this.c(VolunteerLanding);
    return <VolunteerLandingI fid={p.fid} userType={Kasier} nextPage={`/festival/registration/kasier/${p.fid}`}/>;
  }
}


export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<CashierLanding> {
  return regBundleCtx(bundleName, mainContainer, CashierLanding,
      o => o.bind([['festSr', FestSr]]) as FwdContainer);
}
