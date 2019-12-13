import {Thenable} from "async/abortable-promise";
import { RestSr } from "app/service/rest-service";

export interface TokenBalanceView {
  effectiveTokens: number;
  pendingTokens: number;
}

export type TokenRequestId = number;

export interface TokenRequestVisitorView {
  tokenRequestId: TokenRequestId;
  amount: number;
  approved: boolean;
}

export class TokenSr {
  // @ts-ignore
  private $restSr: RestSr;

  getBalance(): Thenable<TokenBalanceView> {
    return this.$restSr.getS('/api/token/myBalance');
  }

  requestTokens(amount: number): Thenable<TokenRequestId> {
    return this.$restSr.postJ(`/api/token/request/${amount}`, {});
  }

  showRequestTokenToVisitor(tokReq: TokenRequestId): Thenable<TokenRequestVisitorView> {
    return this.$restSr.getS(`/api/token/visitor-view/${tokReq}`);
  }
}