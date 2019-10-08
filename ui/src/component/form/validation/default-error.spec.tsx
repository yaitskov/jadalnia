import { h } from 'preact';
import { deep } from 'preact-render-spy';
import { DefaultErr } from './default-error';
import { changeEvent, yieldRedraw } from 'util/test-utils';

import { resolved } from 'async/abortable-promise';
import { InputBox } from 'component/form/input/input-box';
import { InputOk } from 'component/form/input/input-ok';
import { InputCheck } from 'component/form/validation/input-check';
import { InpErr } from 'component/form/validation/input-error';
import { IfErr } from 'component/form/validation/if-error';
import { Container, inject } from 'injection/inject-1k';
import { Sform } from 'component/form/sform';

import { Valiform } from 'component/form/validation/form-validation';
import { Validation } from 'component/form/validation/validation';

describe('DefaultError', () => {
  it('show errors without views', (done) => {
    const container = new Container();

    container
      .bind(
        [
          ['valiform', Valiform],
          ['validation', Validation],
        ])
      .sBean('container', container);


    const SformI = inject(Sform, container);
    const InputOkI = inject(InputOk, container);
    const InputBoxI = inject(InputBox, container);
    const CheckI = inject(InputCheck, container);
    const InpErrI = inject(InpErr, container);
    const IfErrI = inject(IfErr, container);
    const DefaultErrI = inject(DefaultErr, container);

    const data = {action: 'a'};
    const dom = deep(
      <SformI data={data} onSend={e => resolved({})}>
        <InputBoxI>
          <CheckI on="c" mit="!e r:^a$">
            <InputOkI a="action" />
          </CheckI>
          <InpErrI>
            <DefaultErrI/>
          </InpErrI>
        </InputBoxI>
      </SformI>);

    dom.find('[onChange]').simulate('change', changeEvent('b'));

    yieldRedraw(() => {
      expect(dom.find('.err-r').text()).toContain("value doesn't match");
      done();
    });
  });
});
