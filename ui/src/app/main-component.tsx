import { h, Component } from 'preact';

import { o2j } from 'util/json';
import { Router } from 'preact-router';
import AsyncRoute from 'preact-async-route';
import { Container } from 'injection/inject-1k';
import { InjSubCom } from 'injection/inject-sub-components';
import { Instantiable } from 'collection/typed-object';

interface AsyncModule {
  default: (name: string, mainContainer: Container) => Instantiable<Component>;
}

export class MainCom extends InjSubCom<{}, {}> {
  private inj(module: AsyncModule, name: string): Instantiable<Component> {
    return module.default(name, this.$container);
  }

  LPG = async () => await import('./page/landing-page')
    .then(m => this.inj(m as AsyncModule, 'landing-page'));

  Terms = async () => await import('./page/terms-of-service')
    .then(m => this.inj(m as AsyncModule, 'terms-of-service'));

  Privacy = async () => await import('./page/privacy')
    .then(m => this.inj(m as AsyncModule, 'privacy'));

  AdminNotificationPermitRequest = async () =>
    await import('./page/festival/notification/admin-notification-permit-request')
      .then(m => this.inj(m as AsyncModule, 'admin-notification-permit-request'));

  Lang = async () => await import('./page/pick-language')
    .then(m => this.inj(m as AsyncModule, 'pick-language'));

  NewFestival = async () => await import('./page/festival/new-festival')
    .then(m => this.inj(m as AsyncModule, 'new-festival'));

  FestStateCtrl = async() => await import ('./page/festival/control/festival-state-control')
    .then(m => this.inj(m as AsyncModule, 'festival-state-control'));

  AdminCtrlMenu = async () => await import('./page/festival/control/admin-control-menu')
    .then(m => this.inj(m as AsyncModule, 'admin-control-menu'));

  NewInvites = async () => await import('./page/festival/invite/new-invites')
    .then(m => this.inj(m as AsyncModule, 'new-invites'));

  UpdateFestMenuItem = async () => await import('./page/festival/menu/update-fest-menu-item')
    .then(m => this.inj(m as AsyncModule, 'update-fest-menu-item'));

  NewFestMenuItem = async () => await import('./page/festival/menu/new-fest-menu-item')
    .then(m => this.inj(m as AsyncModule, 'new-fest-menu-item'));

  FestMenu = async (u, cb, props) => await import('./page/festival/menu/fest-menu')
    .then(m => {
      console.log(`props  ${o2j(props)}`);
      return this.inj(m as AsyncModule, 'fest-menu');
    });

  TodoList = async () => await import('./todo-list')
    .then(m => this.inj(m as AsyncModule, 'todo-list'));

  NewTodo = async () => await import('./new-todo')
    .then(m => this.inj(m as AsyncModule, 'new-todo'));

  VolunteerCtrl = async() => await import('app/page/festival/volunteer/volunteers-control')
    .then(m => this.inj(m as AsyncModule, 'volunteers-control'));

  WaitersCtrl = async() => await import('app/page/festival/volunteer/waiters-control')
    .then(m => this.inj(m as AsyncModule, 'waiters-control'));

  WaiterLanding = async() => await import('app/page/festival/volunteer/waiter-landing')
    .then(m => this.inj(m as AsyncModule, 'waiter-landing'));

  WaiterReg = async() => await import('app/page/festival/volunteer/kelner-registration')
    .then(m => this.inj(m as AsyncModule, 'kelner-registration'));n

  KelnerTakenOrder = async() => await import('app/page/festival/volunteer/kelner/kelner-taken-order-page')
    .then(m => this.inj(m as AsyncModule, 'kelner-taken-order-page'));

  KelnerServe = async() => await import('app/page/festival/volunteer/kelner-serve')
    .then(m => this.inj(m as AsyncModule, 'kelner-serve'));

  CashierLanding = async() => await import('app/page/festival/volunteer/cashier-landing')
    .then(m => this.inj(m as AsyncModule, 'cashier-landing'));

  CustomerLanding = async() => await import('app/page/festival/customer/customer-landing')
    .then(m => this.inj(m as AsyncModule, 'customer-landing'));

  CustomerMenu = async() => await import('app/page/festival/customer/customer-menu')
    .then(m => this.inj(m as AsyncModule, 'customer-menu'));

  CashiersCtrl = async() => await import('app/page/festival/volunteer/cashiers-control')
    .then(m => this.inj(m as AsyncModule, 'cashiers-control'));

  InviteLinks = async() => await import('app/page/festival/invite/invite-links')
    .then(m => this.inj(m as AsyncModule, 'invite-links'));
  // <Route path='/' component={this.c(Terms)} />

  render() {
    return <Router>
      <AsyncRoute path='/' getComponent={this.LPG} />
      <AsyncRoute path='/festival/kelner/serve/order/:fid/:order' getComponent={this.KelnerTakenOrder}/>
      <AsyncRoute path='/festival/kelner/serve/:fid' getComponent={this.KelnerServe} />
      <AsyncRoute path='/admin/festival/state/control/:fid' getComponent={this.FestStateCtrl} />
      <AsyncRoute path='/festival/invite/cashier/:fid' getComponent={this.CashierLanding} />
      <AsyncRoute path='/festival/registration/kelner/:fid' getComponent={this.WaiterReg} />
      <AsyncRoute path='/festival/visitor/menu/:fid' getComponent={this.CustomerMenu} />
      <AsyncRoute path='/festival/invite/customer/:fid' getComponent={this.CustomerLanding} />
      <AsyncRoute path='/festival/invite/waiter/:fid' getComponent={this.WaiterLanding} />
      <AsyncRoute path='/admin/festival/volunteers/control/:fid' getComponent={this.VolunteerCtrl} />
      <AsyncRoute path='/admin/festival/waiters/:fid' getComponent={this.WaitersCtrl} />
      <AsyncRoute path='/admin/festival/cashiers/:fid' getComponent={this.CashiersCtrl} />
      <AsyncRoute path='/admin/festival/invite-links/:fid' getComponent={this.InviteLinks} />
      <AsyncRoute path='/admin/festival/control/:fid' getComponent={this.AdminCtrlMenu} />
      <AsyncRoute path='/festival/new/notification/:fid' getComponent={this.AdminNotificationPermitRequest} />
      <AsyncRoute path='/festival/new/menu/:fid' getComponent={this.FestMenu} />
      <AsyncRoute path='/admin/festival/new/invites/:fid' getComponent={this.NewInvites} />
      <AsyncRoute path='/festival/menu/item/edit/:fid/:name'
                  getComponent={this.UpdateFestMenuItem} />
      <AsyncRoute path='/admin/festival/menu/new/item/:fid' getComponent={this.NewFestMenuItem} />
      <AsyncRoute path='/festival/new/start' getComponent={this.NewFestival} />
      <AsyncRoute path='/terms' getComponent={this.Terms} />
      <AsyncRoute path='/privacy' getComponent={this.Privacy} />
      <AsyncRoute path='/lang' getComponent={this.Lang} />
      <AsyncRoute path='/todo-list' getComponent={this.TodoList} />
      <AsyncRoute path='/new-todo' getComponent={this.NewTodo} />
    </Router>;
  }
}
