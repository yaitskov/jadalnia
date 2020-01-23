import { DishName } from 'app/types/menu';

export type OrderLabel = string;

export type OrderState = 'Accepted' | 'Paid' | 'Executing' |
  'Ready' | 'Handed' | 'Cancelled' | 'Returned' | 'Abandoned';

export type OrderPayResult = 'NOT_ENOUGH_FUNDS' |  'ALREADY_PAID' |
  'CANCELLED' | 'RETRY' | 'ORDER_PAID' | 'FESTIVAL_OVER';

export interface OrderItem {
  name: DishName;
  quantity: number;
}

export interface KelnerOrderView {
  items: OrderItem[];
}

export interface OrderInfoCustomerView {
  label: OrderLabel;
  price: number;
  state: OrderState;
}

export interface OrderProgress {
  ordersAhead: number;
  etaSeconds: number;
  state: OrderState;
}