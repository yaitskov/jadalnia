import {Admin, UserAuth} from 'app/auth/user-auth';
import { UserRegReq } from 'app/page/sign-up/sign-up-form';
import { Thenable } from 'async/abortable-promise';
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

  public signUp(regReq: UserRegReq): Thenable<Uid> {
    return this.$restSr.postJ<UserSession>('/api/user/register', regReq)
      .tn(rSession => {
        this.$userAuth.storeSession(
          `${rSession.uid}:${rSession.key}`,
          regReq.festivalId,
          regReq.name,
          optS(''),
          regReq.userType);
        console.log(`signed up with ${JSON.stringify(rSession)}`) ;
        return rSession.uid;
      });
  }
}
