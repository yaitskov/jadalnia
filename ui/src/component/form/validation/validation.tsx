import { Tobj } from 'collection/typed-object';
import { Thenable, resolved, tFold } from 'async/abortable-promise';
import { InputCheckP } from 'component/form/validation/input-check-def';
import { Invalid, CheckName, MessageTemplate } from 'component/form/validation/invalid';
import { U } from 'util/const';

export interface Validator {
  name(): CheckName;
  check(val: string): Thenable<Invalid[]>;
}

// every validator instance (for every field it is applied)
// must have dispatcher to know whether validation is applied
export class ValiCache implements Validator {
  // @ts-ignore TS2564
  private input: string | undefined;
  // @ts-ignore TS2564
  private res: Invalid[] | undefined;
  // @ts-ignore TS2564
  private ts: number;
  // @ts-ignore TS2564
  private pro: Thenable<Invalid[]> | undefined;
  // @ts-ignore TS2564
  private err: object | undefined;

  constructor(public base: Validator) {}

  public name(): string {
    return this.base.name();
  }

  public check(val: string): Thenable<Invalid[]> {
    let r: Thenable<Invalid[]>;
    if (this.input === U || this.input !== val) { // first time || different
      this.input = val;
      this.err = U;
      if (this.pro) {
        this.pro.abort();
      }
      r = this.pro = this.base.check(val).tn<Invalid[]>(
        v => {
          if (this.input === val) {
            this.res = v;
            this.ts = Date.now();
            this.pro = U;
          } else {
            console.log('late res ' + val);
          }
          return v;
        }).ctch(
          (e) => {
            if (this.input === val) {
              this.pro = U;
              this.err = e;
              this.res = this.input = U;
            } else {
              console.log('late reject ' + e);
            }
            throw e;
          }) as Thenable<Invalid[]>;
    } else { // input = val
      if (this.res === U) { // result is not ready
        r = this.pro as Thenable<Invalid[]>;
      } else if ((this.res as []).length && Date.now() - this.ts < 800000) {
        r = resolved(this.res as Invalid[]);
      } else {
        this.input = U;
        r = this.check(val);
      }
    }
    return r;
  }
}

export class ValidatorF implements Validator {
  constructor(private n: string,
              private f: (s: string) => Thenable<Invalid[]>) {}

  check(val: string): Thenable<Invalid[]> {
    return this.f(val).tnr(ers => {
      ers.forEach(e => {
        e.check = this.n;
      });
    });
  }

  public name(): string {
    return this.n;
  }
}

abstract class SkipEmptyValidator implements Validator {
  protected abstract chkNE(val: string): Thenable<Invalid[]>;

  public abstract name(): string;

  public check(val: string): Thenable<Invalid[]> {
    return !val ? resolved([]) : this.chkNE(val);
  }
}

abstract class SyncValidator extends SkipEmptyValidator {
  protected abstract valid(val: string): boolean;
  protected abstract msgTpl(): string;

  public abstract name(): string;

  public chkNE(val: string): Thenable<Invalid[]> {
    return resolved(this.syncCheck(val));
  }

  protected syncCheck(val: string): Invalid[] {
    return this.valid(val) ? [] : [new Invalid(this.msgTpl(), this.name(), {})];
  }
}

export class Max extends SyncValidator {
  constructor(public mx: number) {
    super();
  }
  name() { return 'max'; }
  protected valid(val: string): boolean {
    return +val <= this.mx;
  }
  protected msgTpl(): string {
    return `value greater than ${this.mx}`;
  }
}

export class Range extends SkipEmptyValidator {
  constructor(public mn: number, public mx: number) {
    super();
  }

  name(): string { return 'rng'; }

  public chkNE(val: string): Thenable<Invalid[]> {
    return resolved(this.syncCheck(val));
  }

  protected syncCheck(val: string): Invalid[] {
    if (val.length > this.mx) {
      return [new Invalid(`longer than ${this.mx}`, this.name(), {})];
    } else if (val.length < this.mn) {
      return [new Invalid(`shorter than ${this.mn}`, this.name(), {})];
    }
    return [];
  }
}

export class Min extends SyncValidator {
  constructor(public mn: number) {
    super();
  }
  name() { return 'min'; }
  protected valid(val: string): boolean {
    return +val >= this.mn;
  }
  protected msgTpl(): string {
    return `value less than ${this.mn}`;
  }
}

export class Match extends SyncValidator {
  constructor(public p: RegExp) {
    super();
    console.log(`match ${p}`);
  }
  name() { return 'r'; }
  protected valid(val: string): boolean {
    return this.p.test(val);
  }
  protected msgTpl(): string {
    return `value doesn't match ${this.p}`;
  }
}

export class NotEmpty implements Validator {
  name() { return `!e`; }

  public check(val: string): Thenable<Invalid[]> {
    console.log(`not empty val [${val}] => ${!!val}`);
    return resolved(!!val ? [] : [new Invalid('field is required', this.name(), {})]);
  }
}

export class IntType extends SyncValidator {
  name() { return 'i'; }
  protected valid(val: string): boolean {
    return val.match(/^[0-9]{1,10}$/) as any;
  }
  protected msgTpl(): string {
    return `field is not integer number`;
  }
}

export class ValiChain implements Validator {
  constructor(public sub: Validator[]) {
    console.log(`vali chain len = ${sub.length}`);
  }

  name(): string {
    return `validators(${this.sub.map(sv => sv.name()).join(' ')})`;
  }

  check(val: string): Thenable<Invalid[]> {
    return tFold(this.sub.map(i => () => i.check(val)));
  }
}

const valFactories = {
  'min': (l) => new Min(+l),
  'max': (l) => new Max(+l),
  'rng': (mn, mx) => new Range(+mn, +mx),
  '!e': () => new NotEmpty(),
  'i':  () => new IntType(),
  'r': (pattern) => new Match(new RegExp(pattern))
};

const chainSplit = /[ ]/;
const partSplit = /[:]/;

export class Validation {
  private _validators: Map<string, Validator> = new Map<string, Validator>();

  build(vChain: InputCheckP[], fieldName: string): Validator {
    return vChain.length == 1 ? this.mkVali(vChain[0], fieldName)
         : new ValiChain(vChain.map(ic => this.mkVali(ic, fieldName)));
  }

  private mkVali(check: InputCheckP, fieldName: string): Validator {
    const t = typeof check.mit;
    if (t == "string") {
      return this.parseValiExpr(check.mit as string, fieldName);
    }
    if (t == "function") {
      return new ValiCache(
        new ValidatorF('-',
                       check.mit as (string) => Thenable<Invalid[]>));
    }
    throw new Error('bad mit type');
  }

  private parseValiExpr(valiExpr: string, fieldName: string): Validator {
    // group by event type (k c s)
    // parse string defined validators into promise factories
    // for every item define dispatcher
    return new ValiChain(valiExpr
      .split(chainSplit)
      .filter(w => w)
      .map(check => {
        let [name, ...args] = check.split(partSplit);
        const factory = valFactories[name];
        if (!factory) {
          throw new Error(`Bad validator ${name} for field ${fieldName}`);
        }
        return factory(...args);
      }));
  }
}
