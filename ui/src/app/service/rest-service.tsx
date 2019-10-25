import { postJ, geT, PostPayload } from 'async/abortable-fetch';
import { Thenable } from 'async/abortable-promise';
import { UserAuth } from 'app/auth/user-auth';
import { RestErr } from 'component/err/error-types';

const nonJsonBody = (response, ex: Error): any => {
  if (ex instanceof RestErr) {
    throw ex;
  }
  throw new RestErr('n/a', 'raw', `status ${response.status} ${ex.message}`, {});
};

const structuredError = (jsonResp) => {
  if (jsonResp['@type'] == 'Error') {
    throw new RestErr(jsonResp.id, 'raw', jsonResp.message, {});
  } else if (jsonResp['@type'] == 'TemplateError') {
    throw new RestErr(jsonResp.id, 'tpl', jsonResp.message, jsonResp.params);
  } else {
    return jsonResp;
  }
};

export const handleRestResponse = <T extends any >(r): Promise<T> => {
    let contentType = r.headers.get("content-type");
    if (contentType.startsWith("application/json")) {
      return r.json().then(jsonResp => structuredError(jsonResp))
        .catch(ex => nonJsonBody(r, ex));
    } else {
      return r.text().then(
        rawError => { throw new RestErr('n/a', 'raw', rawError, {}); })
    }
};

export class RestSr {
  // @ts-ignore
  private $userAuth: UserAuth;

  postJ<T>(url: string, jsonData: PostPayload): Thenable<T> {
    return postJ(url, jsonData, {session: this.$userAuth.mySession().el('')})
      .tn(handleRestResponse);
  }

  geT<T>(url: string): Thenable<T> {
    return geT(url).tn(handleRestResponse)
  }
}
