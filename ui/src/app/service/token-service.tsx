import {Thenable} from "async/abortable-promise";
import { RestSr } from "app/service/rest-service";

export interface TokenBalanceView {
  effectiveTokens: number;
  pendingTokens: number;
}

export class TokenSr {
  // @ts-ignore
  private $restSr: RestSr;

  getBalance(): Thenable<TokenBalanceView> {
    return this.$restSr.getS('/api/token/myBalance');
  }
}