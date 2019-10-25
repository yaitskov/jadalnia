import { h } from 'preact';
import { postJ } from 'async/abortable-fetch';
import { Thenable } from 'async/abortable-promise';
import { Instantiable } from 'collection/typed-object';
import { regBundle } from 'injection/bundle';
import { Container } from 'injection/inject-1k';
import { MainMenu } from 'app/main-menu';
import { ToDoForm, ToDo } from 'app/todo-form';
import { InjSubCom } from 'injection/inject-sub-components';
import { ObList } from 'collection/observable-list';
import { T } from 'i18n/translate-tag';


function submitHandler(data: ToDo): Thenable<ToDo> {
  console.log(`sending data ${JSON.stringify(data)}`);
  return postJ('/todo', data).tn(
    (r) => {
      console.log(`sent data ${JSON.stringify(data)}`) ;
      return data;
    }).ctch(
      e => console.log(`ops ${e}`));
}


interface TodoGroupS {
  todo: ToDo;
}

export class TodoGroup extends InjSubCom<{}, TodoGroupS> {
  // @ts-ignore
  $bundleName: string;
  // @ts-ignore
  $todoList: ObList<ToDo>;

  constructor(props) {
    super(props);
    this.st = {todo: {priority: 1, action: 'GO GO GO!'}};
  }

  render() {
    const [TI, ToDoFormI]  = this.c2(T, ToDoForm);
    return <div>
      <MainMenu/>
      <h1><TI m="New TODO"/>: {this.$bundleName}</h1>
      <ToDoFormI todo={this.st.todo}
                 onSubmit={td => submitHandler(td).tnr(t => this.$todoList.add(t))}/>
    </div>;
  }
}

export default function loadBundle(bundleName: string, mainContainer: Container)
  : Instantiable<TodoGroup> {
  return regBundle(bundleName, mainContainer, TodoGroup);
}
