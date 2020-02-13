export type TokenPoints = number;

export interface TokenStatsResponse {
  boughtByCustomers: TokenPoints;
  returnedToCustomers: TokenPoints;
  pendingBoughtByCustomers: TokenPoints;
  pendingReturnToCustomers: TokenPoints;
}
