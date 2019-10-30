import {Admin, UserAuth} from 'app/auth/user-auth';
import { UserRegReq } from 'app/page/sign-up/sign-up-form';
import { postJ } from 'async/abortable-fetch';
import { Thenable } from 'async/abortable-promise';
import removeEmptyVals from 'collection/remove-empty-values';
import { CommonUtil } from 'app/common-util';
import { optS } from 'collection/optional';
import { BasicFestInfo } from 'app/page/festival/basic-festival-info';
import {Fid, Uid} from 'app/page/festival/festival-types';
import { RestSr } from 'app/service/rest-service';

interface UserSession {
  uid: Uid;
  key: string;
}
interface AdminSignUpResponse {
  fid: Fid;
  session: UserSession;
}

export class SignUpSr {
  // @ts-ignore
  private $cutil: CommonUtil;
  // @ts-ignore
  private $userAuth: UserAuth;
  // @ts-ignore
  private $restSr: RestSr;

  public signUpAdmin(basicFestInfo: BasicFestInfo): Thenable<Fid> {
    return this.$restSr.postJ<AdminSignUpResponse>('/api/festival/create', basicFestInfo)
      .tn(r => {
        this.$userAuth.storeSession(
          `${r.session.uid}:${r.session.key}`,
          r.fid, basicFestInfo.userName, optS(''), Admin);
        return r.fid;
      });
  }

  public signUp(regReq: UserRegReq): Thenable<Response> {
    return postJ('/api/anonymous/user/register',
                 removeEmptyVals({
                   name: regReq.fullName,
                   phone: regReq.phone,
                   email: regReq.email,
                   sessionPart: this.$cutil.genUserSessionPart()
                 }))
      .tnr(r => r.json()
                 .then(
                   (r) => {
                     // this.$requestStatus.complete(r);
                     this.$userAuth.storeSession(r.session, r.uid, regReq.fullName, optS(regReq.email), r.type || 'admin');
                     console.log(`sent sign up data ${JSON.stringify(regReq)}`) ;
                     return regReq;
                   }))
      .ctch(e => console.log(`ops ${e}`));
  }
}
