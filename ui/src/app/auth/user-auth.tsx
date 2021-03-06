import { LocalStorage } from 'app/persistence/local-storage';
import { Opt } from 'collection/optional';
import { route } from 'preact-router';
import { Fid, Uid } from 'app/page/festival/festival-types';
import {UserType} from "app/service/user-types";

export const MySession = 'mySession';
export const Admin = 'Admin';
export const AdminOfTournaments = 'adminOfTournaments';
export const User = 'customer' as UserType;
export const MyType = 'MyType';
export const MyEmail = 'myEmail';
export const MyName = 'myName';
export const MyFid = 'myFid';

export type Phone = Opt<string>;
export type Email = Opt<string>;

export interface AccountInfo {
  name: string;
  phone: Phone;
  email: Email;
}

export class UserAuth {
  // private returnOnAuth: Opt<stirng> = nic();

  // @ts-ignore
  private $locStore: LocalStorage;

  public isAuthenticated(): boolean {
    return this.$locStore.get(MySession).full;
  }

  public isAdmin(): boolean {
    return this.$locStore.get(Admin).full ||
           this.$locStore.get(AdminOfTournaments).full;
  }

  public userType(): UserType {
    return this.$locStore.get(MyType).map(x => x as UserType).el(User);
  }

  public logout(): void {
    this.$locStore.clearAll();
    route('/');
  }

  public myEmail(): Opt<string> {
    return this.$locStore.get(MyEmail);
  }

  public mySession(): Opt<string> {
    return this.$locStore.get(MySession);
  }

  public myName(): Opt<string> {
    return this.$locStore.get(MyName);
  }

  public myUid(): Uid {
    return this.$locStore.get(MySession).map(session => +session.split(":")[0]).el(0);
  }

  public myFid(): Opt<Fid> {
    return this.$locStore.get(MyFid).map(f => +f);
  }

  public requireLogin(): void {
    //this.returnOnAuth = opt(window.location);
    route('/sign-up');
  }

  public updateAccount(accountInfo: AccountInfo): void {
    accountInfo.email.ifVE(
      email => this.$locStore.store(MyEmail, email),
      () => this.$locStore.drop(MyEmail));
  }

  public storeSession(fullSession: string, fid: Fid, name: string, email: Email, type: UserType): void {
    console.log(`Authenticated as ${fullSession}`);
    this.$locStore.store(MySession, fullSession);
    this.$locStore.store(MyFid, `${fid}`);
    this.$locStore.store(MyName, name);
    this.$locStore.store(MyType, type);
    email.ifV(e => this.$locStore.store(MyEmail, e));
    /* this.returnOnAuth.ifVE(
     *   p => {
     *     route(p);
     *     this.returnOnAuth.cls();
     *   },
     *   () => route('/')); */
  }
}
