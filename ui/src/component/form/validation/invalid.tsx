import { Tobj } from 'collection/typed-object';

export type MessageTemplate = string;
export type CheckName = string;

export class Invalid {
  constructor(public msgTmp: MessageTemplate,
              public check: CheckName,
              public params: Tobj<any>) {}
}
