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

  Lang = async () => await import('./page/pick-language')
    .then(m => this.inj(m as AsyncModule, 'pick-language'));

  NewFestival = async () => await import('./page/festival/new-festival')
    .then(m => this.inj(m as AsyncModule, 'new-festival'));

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

  SignUp = async () => await import('app/page/sign-up/sign-up')
    .then(m => this.inj(m as AsyncModule, 'sign-up'));

  TodoList = async () => await import('./todo-list')
    .then(m => this.inj(m as AsyncModule, 'todo-list'));

  NewTodo = async () => await import('./new-todo')
    .then(m => this.inj(m as AsyncModule, 'new-todo'));

  // <Route path='/' component={this.c(Terms)} />

  render() {
    return <Router>
      <AsyncRoute path='/' getComponent={this.LPG} />
      <AsyncRoute path='/admin/festival/control/:fid' getComponent={this.AdminCtrlMenu} />
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
      <AsyncRoute path='/sign-up' getComponent={this.SignUp} />
    </Router>;
  }
}
