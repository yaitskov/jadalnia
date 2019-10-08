import { h, Component } from 'preact';

import { Router, Route, route } from 'preact-router';
import AsyncRoute from 'preact-async-route';
import { inject, Container } from 'injection/inject-1k';
import { InjSubCom } from 'injection/inject-sub-components';
import { Instantiable, gNew, Tobj, keysM } from 'collection/typed-object';
//import { I18Trans } from 'i18n/i18n-translator';

interface AsyncModule {
  default: (name: string, mainContainer: Container) => Instantiable<Component>;
  //  default: Instantiable<Component>;
}

//import { LandingPage } from 'app/landing-page';

export class MainCom extends InjSubCom<{}, {}> {
  // @ts-ignore
  // $bundlesCtx: Tobj<FwdContainer>;

  private inj(module: AsyncModule, name: string): Instantiable<Component> {
    return module.default(name, this.$container);
  }

  LPG = async () => await import('./page/landing-page')
    .then(m => this.inj(m as AsyncModule, 'landing-page'));

  Terms = async () => await import('./page/terms-of-service')
    .then(m => this.inj(m as AsyncModule, 'terms-of-service'));

  Privacy = async () => await import('./page/privacy')
    .then(m => this.inj(m as AsyncModule, 'privacy'));

  Features = async () => await import('./page/features')
    .then(m => this.inj(m as AsyncModule, 'features'));

  Lang = async () => await import('./page/pick-language')
    .then(m => this.inj(m as AsyncModule, 'pick-language'));

  ChooseSport = async () => await import('./page/tournament/choose-sport')
    .then(m => this.inj(m as AsyncModule, 'choose-sport'));

  NewTennisTour = async () => await import('./page/tournament/new/tennis')
    .then(m => this.inj(m as AsyncModule, 'tennis'));

  NewTennisTourRules = async () => await import('./page/tournament/new/new-tennis-rules')
    .then(m => this.inj(m as AsyncModule, 'new-tennis-rules'));

  SignUp = async () => await import('app/page/sign-up/sign-up')
    .then(m => this.inj(m as AsyncModule, 'sign-up'));

  TodoList = async () => await import('./todo-list')
    .then(m => this.inj(m as AsyncModule, 'todo-list'));

  NewTodo = async () => await import('./new-todo')
    .then(m => this.inj(m as AsyncModule, 'new-todo'));

  // SignIn = async () => await import('./app/page/sign-in').then(m => this.inj(m as AsyncModule, 'sign-in'));
  // <AsyncRoute path='/sign-in' getComponent={this.SignIn} />
  // <Route path='/' component={this.c(Terms)} />

  render() {
    return <Router>
      <AsyncRoute path='/' getComponent={this.LPG} />
      <AsyncRoute path='/tournament/new/tennis/rules' getComponent={this.NewTennisTourRules} />
      <AsyncRoute path='/tournament/new/tennis' getComponent={this.NewTennisTour} />
      <AsyncRoute path='/tournament/new/choose-sport' getComponent={this.ChooseSport} />
      <AsyncRoute path='/terms' getComponent={this.Terms} />
      <AsyncRoute path='/features/all' getComponent={this.Features} />
      <AsyncRoute path='/privacy' getComponent={this.Privacy} />
      <AsyncRoute path='/lang' getComponent={this.Lang} />
      <AsyncRoute path='/todo-list' getComponent={this.TodoList} />
      <AsyncRoute path='/new-todo' getComponent={this.NewTodo} />
      <AsyncRoute path='/sign-up' getComponent={this.SignUp} />
    </Router>;
  }
}
