import { h } from 'preact';
import { InjSubCom } from 'injection/inject-sub-components';
import { T } from 'i18n/translate-tag';
import { UserAuth } from 'app/auth/user-auth';

import bulma from 'bulma/bulma.sass';

export class LogoutBtn extends InjSubCom<{}, {}> {
  // @ts-ignore
  private $userAuth: UserAuth;

  constructor(props) {
    super(props);
    this.onClick = this.onClick.bind(this);
  }

  onClick(e) {
    this.$userAuth.logout();
  }

  render() {
      const TI = this.c(T);
      return <button class={bulma.button + ' ' + bulma.isPrimary}
                     onClick={this.onClick}>
        <strong>
          <TI m="logout"/>
        </strong>
      </button>;
  }
}
