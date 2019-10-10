import { h } from 'preact';
import { InjSubCom } from 'injection/inject-sub-components';
import { Container } from 'injection/inject-1k';
import { regBundleCtx } from 'injection/bundle';
import { Instantiable } from 'collection/typed-object';
import { LocalStorage } from 'app/persistence/local-storage';
import { NewFestival } from 'app/page/festival/festival-types';

class SetMenu extends InjSubCom<{}, {}> {
  // @ts-ignore
  private $locStore: LocalStorage;

  render() {
    return this.$locStore.jGet<NewFestival>('newFestival')
      .map(newFest =>
        <div>
          <p>menu stub</p>
        </div>)
      .elf(() => <div>no basic info about festival</div>);
  }
}

export default function loadBundle(bundleName: string, mainContainer: Container): Instantiable<SetMenu> {
  return regBundleCtx(bundleName, mainContainer, SetMenu, (o) => o);
}
