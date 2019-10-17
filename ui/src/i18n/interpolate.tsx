import { Tobj, ifStrE, isObj, isStr, isAr } from 'collection/typed-object';

// Wow ! recursive types. Haskell cannot do THIS, but TypeScript CAN!
export type Msg = string | Cases | MsgList;
export interface Cases extends Tobj<Msg> {};
export interface MsgList extends Array<Msg> {};

type StringOrNum = string | number;
export type EvalParams = Tobj<StringOrNum>;

const _evalTpl = (tpl: Msg, params: EvalParams, buf: StringOrNum[]) => {
  ifStrE(tpl, s => buf.push(s),
        () => {
          if (isAr(tpl)) {
            const r = [];
            for (let i = 0; i < tpl.length; ++i) {
              const item = tpl[i];
              if (item.length < 6 && item in params) {
                const nextItem = tpl[i + 1];
                const val = params[item];
                if (isObj(nextItem)) {
                  i++;
                  if (val in nextItem) {
                    _evalTpl(nextItem[val], params, buf);
                  } else {
                    buf.push(val);
                  }
                } else {
                  buf.push(val);
                }
              } else {
                buf.push(item);
              }
            }
          }
        });
};

export const evalTpl = (tpl: Msg, params: EvalParams): string => {
  const r: StringOrNum[] = [];
  _evalTpl(tpl, params, r);
  return r.join('');
};
