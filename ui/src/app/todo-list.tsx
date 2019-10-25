import { h } from 'preact';

import { geT } from 'async/abortable-fetch';
import { Instantiable } from 'collection/typed-object';
import { regBundle } from 'injection/bundle';
import { Container } from 'injection/inject-1k'
import { InjSubCom } from 'injection/inject-sub-components';
import { ToDo } from 'app/todo-form';
import { ObList } from 'collection/observable-list';
import { MainMenu } from 'app/main-menu';
import { T } from 'i18n/translate-tag';

import css from './todo-list.css';

interface TodoListSt {
  todos: ToDo[];
}

export class TodoList extends InjSubCom<{}, TodoListSt> {
  // @ts-ignore
  $bundleName: string;
  // @ts-ignore
  $todoList: ObList<ToDo>;

  constructor(props) {
    super(props);
    this.st = {todos: []};
  }

  wMnt() {
    // geT('/todos').tn(r => r.json().then(todos => this.ust(state => ({...state, todoList: todos.todos}))));
    // this.$todoList.length
    this.$todoList.onAdd(td => this.ust(st => ({...st, todos: [...st.todos, td]})));
    geT('/todos').tn(
      r => r.json().then(
        todos => todos.todos.forEach(
          td => this.$todoList.add(td))));
  }

  render() {
    const TI = this.c(T);
    console.log(`bordered ${JSON.stringify(css.toString())}`);
    return <div>
      <MainMenu/>
      <h1><TI m="Todos"/></h1>
      <p>Bundle: {this.$bundleName}</p>
      <ul>
        {this.st.todos.map(td => <li class={css.bordered}><b>{td.priority}:</b> {td.action}</li>)}
      </ul>
    </div>;
  }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<TodoList> {
  return regBundle(bundleName, mainContainer, TodoList);
}
