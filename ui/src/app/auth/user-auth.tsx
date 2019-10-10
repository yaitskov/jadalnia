import { LocalStorage } from 'app/persistence/local-storage';
import { Opt, nic } from 'collection/optional';
import { route } from 'preact-router';

export const MySession = 'mySession';
export const Admin = 'admin';
export const AdminOfTournaments = 'adminOfTournaments';
export const User = 'User';
export const MyType = 'MyType';
export const MyEmail = 'myEmail';
export const MyName = 'myName';
export const MyFid = 'myFid';

export type Fid = string;
export type UserType = string;

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
    return this.$locStore.get(MyType).el(User);
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

  public myFid(): Opt<string> {
    return this.$locStore.get(MyFid);
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
    this.$locStore.store(MyFid, fid);
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
