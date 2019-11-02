import { h } from 'preact';
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Fid} from 'app/page/festival/festival-types';
import {FestSr} from "app/service/fest-service";
import {InjSubCom} from "injection/inject-sub-components";
import {VolunteerLanding} from "app/page/festival/volunteer/volunteer-landing";
import {Kelner} from "../../../service/user-types";

class WaiterLanding extends InjSubCom<{fid: Fid}, {}> {
  render(p, st) {
    const VolunteerLandingI = this.c(VolunteerLanding);
    return <VolunteerLandingI fid={p.fid} userType={Kelner} nextPage={`/festival/registration/kelner/${p.fid}`}/>;
  }
}


export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<WaiterLanding> {
  return regBundleCtx(bundleName, mainContainer, WaiterLanding,
      o => o.bind([['festSr', FestSr]]) as FwdContainer);
}
