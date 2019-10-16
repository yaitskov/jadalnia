import { postJ } from 'async/abortable-fetch';
import { Thenable } from 'async/abortable-promise';
import { UserAuth } from 'app/auth/user-auth';
import {Tobj} from "collection/typed-object";


const nonJsonBody = (response, ex: Error): any => {
  if (ex instanceof RestErr) {
    throw ex;
  }
  throw new RestErr('non-json', `status ${response.status} ${ex.message}`, {});
};

type ErrorId = string;

class RestErr extends Error {
  constructor(public id: ErrorId,
              public message: string,
              public params: Tobj<any>) {
    super(message);
  }
}

const structuredError = (err) => {
  if (err['@type'] == 'Error') {
    throw new RestErr(err.id, err.message, {});
  } else if (err['@type'] == 'TemplateError') {
    throw new RestErr(err.id, err.message, err.params);
  } else {
    throw new RestErr('no-id', err.message, {});
  }
};

export class RestSr {
  // @ts-ignore
  private $userAuth: UserAuth;

  postJ<T>(url: string, jsonData: {}): Thenable<T> {
    return postJ(url, jsonData, {session: this.$userAuth.mySession().el('')})
      .tn(response => {
        if (response.status < 300) {
          return response.json() as Promise<T>;
        } else {
          return (response.json().then(error => structuredError(error))
            .catch(ex => nonJsonBody(response, ex)));
        }
      });
  }
}
