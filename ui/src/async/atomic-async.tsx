import { Thenable } from 'async/abortable-promise';
import { Opt, nic } from 'collection/optional';
import { Tobj, deepEq } from 'collection/typed-object';

export type ParamsType = Tobj<string>;
export type ParametersF = () => ParamsType;
export type ThenFactory<T> = (s: ParamsType) => Thenable<T>;

export class AtomicAsync<T> {
  private params: Opt<ParamsType> = nic();
  private tn: Opt<Thenable<T>> = nic();

  constructor(private paramsF: ParametersF,
              private thenF: ThenFactory<T>) {
  }

  public val(): Thenable<T> {
    const newParams = this.paramsF();
    if (this.tn.empty || !deepEq(newParams, this.params.val)) {
      this.params.val = newParams;
      this.tn.val = this.thenF(newParams).ctch(e => {
        this.tn.cls();
        throw e;
      });
    }
    return this.tn.val;
  }
}
