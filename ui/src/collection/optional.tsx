export const opt = <T extends {} >(v: T | null | undefined): Opt<T> => new Opt(v);
export const optS = (v: string | null): Opt<string> => new Opt(v ? v : null);
export const nic = <T extends {} >(): Opt<T> => opt<T>(null);

export class Opt<T> {
  private has: boolean;

  constructor(private v: T | null | undefined) {
    this.has = v !== null && v !== undefined;
  }

  public get empty(): boolean {
    return !this.has;
  }

  public el(e: T): T {
    return this.has ? (this.v as T) : e;
  }

  public elf(f: () => T): T {
    return this.has ? (this.v as T) : f();
  }

  public get full(): boolean {
    return this.has;
  }

  public map<B>(f: (v: T) => B): Opt<B> {
    return this.ifVE<Opt<B>>(v => opt(f(v)), nic);
  }

  public get val(): T {
    if (this.has) {
      return this.v as T;
    }
    throw new Error('no val');
  }

  public set val(newVal: T) {
    this.has = true;
    this.v = newVal;
  }

  public cls(): void {
    this.has = false;
  }

  public ifV(f: (v: T) => void): void {
    if (this.has) {
      f(this.v as T);
    }
  }

  public ifVE<B>(f: (v: T) => B, o: () => B): B {
    return this.has ? f(this.v as T) : o();
  }
}
