import { j2o, o2j } from 'util/json';
import { Tobj } from 'collection/typed-object';
import { Opt, opt } from 'collection/optional';

export class LocalStorage {
  private ls: Storage;

  constructor() {
    this.ls = localStorage;
  }

  public store(key: string, value: string): void {
    this.ls.setItem(key, value);
  }

  public get(key: string): Opt<string> {
    return opt(this.ls.getItem(key));
  }

  public jGet<T>(key: string): Opt<T> {
    return this.get(key).map(s => j2o<T>(s));
  }

  public jStore<T>(key: string, value: T): void {
    this.store(key, o2j(value));
  }

  public clearAll(): void {
    while (this.ls.length > 0) {
      this.ls.removeItem(this.ls.key(0) as string);
    }
  }

  public drop(key: string): void {
    this.ls.removeItem(key);
  }

  public allKeys(): string[] {
    const result: string[] = [];
    for (var i = 0; i < this.ls.length; ++i) {
      result.push(this.ls.key(i) as string);
    }
    return result;
  }

  public asMap(): Tobj<string> {
    const result = {};
    this.allKeys().forEach(key => this.get(key).ifV(v => result[key] = v));
    return result;
  }
}
