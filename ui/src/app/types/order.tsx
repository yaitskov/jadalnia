import { DishName } from 'app/types/menu';

export type OrderLabel = string;

export interface OrderItem {
  name: DishName;
  quantity: number;
}

export interface KelnerOrderView {
  items: OrderItem[];
}