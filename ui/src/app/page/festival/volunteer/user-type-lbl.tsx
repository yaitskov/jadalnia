import { h } from 'preact';
import {InjSubCom} from "injection/inject-sub-components";
import {UserType, Kasier, Kelner} from "app/service/user-types";
import { T } from 'i18n/translate-tag';

export interface UserTypeLblP {
  userType: UserType;
}

export class UserTypeLbl extends InjSubCom<UserTypeLblP, {}> {
  render(p) {
    let TI = this.c(T);
    let map = [[Kasier, <TI m="Cashier"/>], [Kelner, <TI m="Waiter"/>]];
    return (map.find(item => item[0] == p.userType) || [0, false])[1];
  }
}
