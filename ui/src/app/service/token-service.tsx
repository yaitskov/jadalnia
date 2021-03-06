import {Thenable} from "async/abortable-promise";
import { RestSr } from "app/service/rest-service";
import {Uid} from "app/page/festival/festival-types";

export interface TokenBalanceView {
  effectiveTokens: number;
  pendingTokens: number;
}

export interface TokenRequestApprove {
  customer: Uid;
  tokens: TokenRequestId[];
}

export type TokenRequestId = number;

export interface TokenRequestVisitorView {
  tokenRequestId: TokenRequestId;
  amount: number;
  approved: boolean;
}

export interface TokenRequestForApprove {
  tokenId: TokenRequestId;
  amount: number;
}

export interface KasierHistoryRecord {
  tokenId: TokenRequestId;
  amount: number;
}

export interface KasierTokenView {
  tokenId: TokenRequestId;
  amount: number;
  approvedAt: string;
  cancelledBy?: TokenRequestId;
  customer: Uid;
}

export type TokenCancelOutcome = 'CANCELLED' | 'NOT_ENOUGH_FUNDS';

export class TokenSr {
  // @ts-ignore
  private $restSr: RestSr;

  showRequestTokenToKasier(tok: TokenRequestId): Thenable<KasierTokenView> {
    return this.$restSr.getS(`/api/token/cashier-view/${tok}`);
  }

  cancelToken(tok: TokenRequestId): Thenable<TokenCancelOutcome> {
    return this.$restSr.postJ(`/api/token/cancel-approved/${tok}`, {});
  }

  getBalance(): Thenable<TokenBalanceView> {
    return this.$restSr.getS('/api/token/myBalance');
  }

  kasierRequestHistory(page: number): Thenable<KasierHistoryRecord[]> {
    return this.$restSr.getS(`/api/token/request-kasier-history/${page}`);
  }

  requestTokens(amount: number): Thenable<TokenRequestId> {
    return this.$restSr.postJ(`/api/token/request/${amount}`, {});
  }

  requestTokenReturn(amount: number): Thenable<TokenRequestId> {
    return this.$restSr.postJ(`/api/token/request-return/${amount}`, {});
  }

  approveSelectedRequest(data: TokenRequestApprove): Thenable<TokenRequestForApprove[]> {
    return this.$restSr.postJ(`/api/token/approve`, data);
  }

  findTokenRequestsByUid(vid: Uid): Thenable<TokenRequestForApprove[]> {
    return this.$restSr.getS(`/api/token/list-for-approve/${vid}`);
  }

  showRequestTokenToVisitor(tokReq: TokenRequestId): Thenable<TokenRequestVisitorView> {
    return this.$restSr.getS(`/api/token/visitor-view/${tokReq}`);
  }
}