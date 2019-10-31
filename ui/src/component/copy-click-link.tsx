import { h } from 'preact';
import {Link} from "preact-router";
import {MyCo} from "component/my-component";
import copy from 'clipboard-copy';
import {siteUrl, isAbsUrl} from "util/routing";
import { CopyCo }  from 'component/icons/copy/copy-co';
import bulma from 'app/style/my-bulma.sass';
import { jne } from 'collection/join-non-empty';

export interface CpClickLnkP {
  url: string;
  t$lbl: string;
  classes?: string;
  onCopied?: () => void;
  onFailed?: (e: Error) => void;
}

export class CpClickLnk extends MyCo<CpClickLnkP, {absUrl: string}> {
  constructor(props) {
    super(props);
    let url = props.url;
    this.st = {absUrl: isAbsUrl(url) ? url : siteUrl() + url };
    this.onClick = this.onClick.bind(this);
  }

  onClick(e) {
    e.preventDefault();
    e.stopImmediatePropagation();
    copy(this.st.absUrl)
      .then(this.props.onCopied || (() => {}))
      .catch(this.props.onFailed || ((_: Error) => {}));
    return false;
  }

  render(p, st) {
    return <Link href={st.absUrl}
                 title="click to copy"
                 class={p.classes || jne(bulma.button, bulma.isText, bulma.isSuccess)}
                 onClick={this.onClick}>
      { p.t$lbl }
      <CopyCo />
    </Link>;
  }
}
