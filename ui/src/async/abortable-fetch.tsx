import { Thenable, AbrPro } from './abortable-promise';

const f = (req: string | Request): Thenable<Response> => {
  const ctrl = new AbortController();
  return new AbrPro<Response>(fetch(req, {signal: ctrl.signal}), ctrl);
};

export const geT = (url: string): Thenable<Response> => f(url);
export const postJ = (url: string, json: {}): Thenable<Response> => f(
  new Request(url,
              {
                method: 'POST',
                headers: { "Content-Type": "application/json" },
                redirect: "follow",
                cache: 'no-cache',
                body: JSON.stringify(json)
              }));
