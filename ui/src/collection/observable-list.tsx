export type OnAdd<T> = (t: T) => void;

export class ObList<T> {
  private _onAdd: OnAdd<T>[] = [];

  public onAdd(cb: OnAdd<T>): ObList<T> {
    this._onAdd.push(cb);
    return this;
  }

  public add(item: T): ObList<T> {
    this._onAdd.forEach(cb => cb(item));
    return this;
  }
}
