import {Tobj} from "collection/typed-object";

export type ErrorId = string;
export type ErrFormat = 'tpl' | 'raw';

export class RestErr extends Error {
  constructor(public id: ErrorId,
              public fmt: ErrFormat,
              public message: string,
              public params: Tobj<any>) {
    super(message);
  }
}


