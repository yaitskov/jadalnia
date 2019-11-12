import { h } from 'preact';
import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Fid} from 'app/page/festival/festival-types';
import {FestSr} from "app/service/fest-service";
import {InjSubCom} from "injection/inject-sub-components";
import {Kelner} from "app/service/user-types";
import {VolunteerRegistrationForm} from "app/page/festival/volunteer/volunteer-registration-form";

class KelnerRegistration extends InjSubCom<{fid: Fid}, {}> {
  render(p, st) {
    const VolunteerRegistrationFormI = this.c(VolunteerRegistrationForm);
    return <VolunteerRegistrationFormI fid={p.fid}
                                       userType={Kelner}
                                       nextPage={`/festival/kelner/serve/${p.fid}`}/>;
  }
}


export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KelnerRegistration> {
  return regBundleCtx(bundleName, mainContainer, KelnerRegistration,
      o => o.bind([['festSr', FestSr]]) as FwdContainer);
}
