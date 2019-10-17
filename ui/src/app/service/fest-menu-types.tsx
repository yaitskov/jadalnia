
export interface MenuItemView {
  name: string;
  disabled: boolean;
  price: number;
  description: string;
}

export interface FestMenuItemFull extends MenuItemView {
}

export const emptyFestMenuItem = () => (
    {name: 'danie', disabled: false, price: 1, description: 'smacznę'});
