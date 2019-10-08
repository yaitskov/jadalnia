import { h } from 'preact';
import { Link } from 'preact-router';
import { MyCo } from 'component/my-component';

export class MainMenu extends MyCo<{}, {}> {
  render() {
    return <div>
      <span>
        <Link activeClassName="active" href="/">Terms</Link>
      </span>
      <span>
        <Link activeClassName="active" href="/new-todo">New Todo</Link>
      </span>
      <span>
        <Link activeClassName="active" href="/todo-list">Todo List</Link>
      </span>
    </div>;
  }
}
