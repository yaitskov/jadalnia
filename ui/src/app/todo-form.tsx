import { h } from 'preact';
import { TransCom, TransComS } from 'i18n/trans-component';
import { Sform } from 'component/form/sform';
import { Submit } from 'component/form/submit';
import { ActionField } from 'app/action-field';
import { PriorityField } from 'app/priority-field';
import { Thenable } from 'async/abortable-promise';

export interface ToDo {
  action: string;
  priority: number;
}

export interface ToDoFormP {
  onSubmit: (d: ToDo) => Thenable<any>;
  todo: ToDo;
}

export class ToDoForm extends TransCom<ToDoFormP, TransComS> {
  constructor(props) {
    super(props);
    this.st = {at: this.at()};
  }

  render() {
    const [SformI, SubmitI, ActionFieldI, PriorityFieldI] =
      this.c4(Sform, Submit, ActionField, PriorityField);

    return <SformI data={this.props.todo}
                   onSend={e => this.props.onSubmit(this.props.todo)}>
      <ActionFieldI></ActionFieldI>
      <PriorityFieldI></PriorityFieldI>
      <SubmitI t$text="apply" />
    </SformI>;
  }

  at(): string[] { return []; }
}
