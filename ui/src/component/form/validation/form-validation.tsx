import { U } from 'util/const';
import removeEmptyVals from 'collection/remove-empty-values';
import { Tobj, forM, mapO } from 'collection/typed-object';
import { InputIf, ValiFieldLi } from 'component/form/validation/input-if';
import { tJoin, tFold, Thenable } from 'async/abortable-promise';
import { Container, inject } from 'injection/inject-1k';
import { InputCheckP, CheckOn } from 'component/form/validation/input-check-def';
import { Invalid } from 'component/form/validation/invalid';
import { Validation, Validator } from 'component/form/validation/validation';
import { grpBy } from 'collection/group-by';

export class MetaInput {
  constructor(public input: InputIf,
              public check: Tobj<Validator>, // key CheckOn
              public fans: ValiFieldLi[] = []) {}

  public shownChecks = (): string[] => this.fans.map(f => f.chkN()).filter(n => !!n);
}

export const failIf = (c: any, msg: string) => {
  if (c) {
    throw new Error(msg);
  }
};

export class FormLevel {
  constructor(private validation: Validation) {}

  private data: Tobj<string> = {};
  private liBuf: ValiFieldLi[] = [];
  private inpChecks: InputCheckP[] = [];
  // @ts-ignore TS2564
  private curInput: InputIf;
  private inputByName: Tobj<MetaInput> = {};
  // @ts-ignore TS2564
  private onSubmit: (d: any) => void;

  public check(c: InputCheckP): void {
    failIf(this.curInput, 'wrap input into checks');
    this.inpChecks.push(c);
  }

  public curField(): string {
    return this.curInput.getProps().a;
  }

  public curMField(): MetaInput {
    return this.metaOf(this.curField());
  }

  public metaOf(field: string): MetaInput {
    return this.inputByName[field];
  }

  public add(input: InputIf): void {
    this.curInput = input;
    const props = input.getProps();
    const checks: Tobj<Validator> = {};
    forM(grpBy(c => c.on, this.inpChecks),
         ([kOn, vLst]) => {
           checks[kOn] = this.validation.build(vLst, props.a);
         });
    this.inputByName[props.a] = new MetaInput(input, checks);
    this.inpChecks = [];
  }

  rm(input: InputIf): void {
    delete this.inputByName[input.getProps().a];
  }

  public checkFieldBy(input: InputIf, events: CheckOn[], newV: string): void {
    const p = input.getProps();
    this.data[p.a] = newV;
    const meta: MetaInput = this.inputByName[p.a];
    if (meta) {
      this.checkBy(events, meta);
    }
  }

  private checkBy(events: CheckOn[], meta: MetaInput): Thenable<Invalid[]> {
    const fieldName = meta.input.getProps().a;
    const fieldValue = this.data[fieldName];
    meta.fans.forEach(f => f.empty());
    return tFold(events.filter(et => et in meta.check).map(et =>
      () => meta.check[et].check(fieldValue))).tnr(errors => {
        if (errors.length) {
          meta.fans.forEach(f => f.invalid(errors));
        } else if (fieldValue !== '') {
          meta.fans.forEach(f => f.valid());
        }
      });
  }

  change(input: InputIf, oldV: string, newV: string): void {
    const p = input.getProps();
    this.data[p.a] = newV;
    const meta: MetaInput = this.inputByName[p.a];
    if (meta) {
      this.checkBy(['k', 'c'], meta); //.tn(o => meta.fans.forEach(f => f.forceUpdate()));
    }
  }

  setValue(data: {}): void {
    this.data = data;
    forM(data, ([k, v]) => {
      const meta = this.inputByName[k];
      if (meta) {
        meta.input.updateVal(data[k]);
      }
    });
    forM(this.inputByName, ([k, meta]) => {
      if (!(k in data)) {
        meta.input.empty();
        meta.fans.forEach(f => f.empty());
      }
    });
  }

  setSubmit(callback: (d: any) => void) {
    this.onSubmit = callback;
  }

  public trySubmit(e): void {
    tJoin(mapO(this.inputByName, ([k, m]) => this.checkBy(['k', 'c', 's'], m))).tn(
      errs => errs.flat()).tn(
        errs => {
          if (errs.length) {
            console.log('sync validators fails');
          } else {
            this.onSubmit(removeEmptyVals({...this.data}));
          }
        });
  }

  addFan(fan: ValiFieldLi) {
    if (this.curInput) {
      this.inputByName[this.curInput.getProps().a].fans.push(fan);
    }
  }

  public flush(): void {
    if (this.curInput) {
      this.liBuf = [];
      this.curInput = U;
    } else {
      throw new Error('InputBox without input');
    }
  }

  public noListeners(): void {
    failIf(this.liBuf.length, 'input listeners is not empty');
    failIf(this.curInput, 'nested InputBox is not supported');
  }
}

export class Valiform  {
  // @ts-ignore
  private $validation: Validation;
  private formStack: FormLevel[] = [];

  public newForm(): void {
    this.formStack.unshift(new FormLevel(this.$validation));
  }

  public endForm(): void {
    this.formStack.shift();
  }

  public topForm(): FormLevel {
    return this.formStack[0];
  }
}
