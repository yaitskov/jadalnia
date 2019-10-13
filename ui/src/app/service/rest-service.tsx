import { postJ } from 'async/abortable-fetch';
import { Thenable } from 'async/abortable-promise';
import { UserAuth } from 'app/auth/user-auth';

export class RestSr {
  // @ts-ignore
  private $userAuth: UserAuth;

  postJ(url: string, jsonData: {}): Thenable<{}> {
    return postJ(url, jsonData, {session: this.$userAuth.mySession().el('')})
      .tn(response => response.json());
  }
}
