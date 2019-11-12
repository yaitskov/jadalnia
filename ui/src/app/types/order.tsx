import { DishName } from 'app/types/menu';

export type OrderLabel = string;

export interface OrderItem {
  name: DishName;
}

export interface KelnerOrderView {
  items: OrderItem[];
}