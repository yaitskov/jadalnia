import { h } from 'preact';

import {Container, FwdContainer} from 'injection/inject-1k';
import { regBundleCtx} from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import {Fid} from 'app/page/festival/festival-types';
import {FestSr} from "app/service/fest-service";
import {InjSubCom} from "injection/inject-sub-components";
import {Kasier} from "app/service/user-types";
import {VolunteerRegistrationForm} from "app/page/festival/volunteer/volunteer-registration-form";

class KasierRegistration extends InjSubCom<{fid: Fid}, {}> {
  render(p, st) {
    const VolunteerRegistrationFormI = this.c(VolunteerRegistrationForm);
    return <VolunteerRegistrationFormI fid={p.fid}
                                       userType={Kasier}
                                       nextPage={`/festival/kasier/serve/${p.fid}`}/>;
  }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<KasierRegistration> {
  return regBundleCtx(bundleName, mainContainer, KasierRegistration,
      o => o.bind([['festSr', FestSr]]) as FwdContainer);
}
