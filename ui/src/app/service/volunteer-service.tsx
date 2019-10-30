import { Thenable } from 'async/abortable-promise';
import { Fid, Uid } from 'app/page/festival/festival-types';
import { RestSr } from "app/service/rest-service";
import { UserType } from 'app/service/user-types';

export interface UserInfo {
  uid: Uid
  name: string;
}

export class VolunteerSr {
  // @ts-ignore
  private $restSr: RestSr;

  listByType(fid: Fid, userType: UserType): Thenable<UserInfo[]> {
    return this.$restSr.geT(`/api/user/list/${fid}/type/${userType}`);
  }
}
