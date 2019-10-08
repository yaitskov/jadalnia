import { Component } from 'preact';
import { Instantiable, gNew,Tobj } from 'collection/typed-object';
import { inject, Container, FwdContainer } from 'injection/inject-1k';
import { I18Trans } from 'i18n/i18n-translator';

export function regBundleCtx<T extends Component>(
  bundleName: string,
  mainContainer: Container,
  componentClass: Instantiable<T>,
  bundleCtxBinder: (subCtx: FwdContainer) => FwdContainer):
Instantiable<T> {
  return inject(componentClass,
                gNew(mainContainer.get('bundlesCtx') as Tobj<FwdContainer>,
                     bundleName,
                     () => bundleCtxBinder(new FwdContainer(mainContainer)
                       .sBean('bundleName', bundleName)
                       .sBeanInj('i18Trans', new I18Trans()) as FwdContainer)));
}
