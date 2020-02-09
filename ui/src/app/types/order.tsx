import { DishName } from 'app/types/menu';
import { MenuItemView } from 'app/service/fest-menu-types';

export type OrderLabel = string;

export type OrderState = 'Accepted' | 'Paid' | 'Executing' |
  'Ready' | 'Handed' | 'Cancelled' | 'Returned' | 'Abandoned';

export type OrderPayResult = 'NOT_ENOUGH_FUNDS' |  'ALREADY_PAID' |
  'CANCELLED' | 'RETRY' | 'ORDER_PAID' | 'FESTIVAL_OVER';

export type UpdateAttemptOutcome = 'UPDATED' | 'FESTIVAL_OVER' |
  'BAD_ORDER_STATE' | 'NOT_ENOUGH_FUNDS' | 'RETRY';

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
  items: OrderItem[];
}

export interface OrderProgress {
  ordersAhead: number;
  etaSeconds: number;
  state: OrderState;
}

export interface OrderUpdate {
  label: OrderLabel;
  newItems: OrderItem[];
}

export const orderItems = (menu: MenuItemView[], mealSelections: number[]) =>
  mealSelections.map(
    (count: number, idx: number) => ({
      name: menu[idx].name, quantity: count
    })).filter(item => item.quantity > 0);

export const sumMealsPrice = (menu: MenuItemView[], mealSelections: number[]) =>
  mealSelections.reduce(
    (sum: number, count: number, idx: number) => sum + menu[idx].price * count, 0);

export const findSelectedMeals = (menu: MenuItemView[], items: OrderItem[]) => {
  let selectedNames = items.map(i => i.name);
  return menu.map((mItem: MenuItemView) => {
    let idx = selectedNames.indexOf(mItem.name);
    if (idx < 0) {
      return 0;
    } else {
      return items[idx].quantity;
    }
  });
};
