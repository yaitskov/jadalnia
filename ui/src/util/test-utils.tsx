import { Thenable } from 'async/abortable-promise';

export const die = (msg: string) => { throw new Error(msg); };

export const unreachable = () => die("Unreachable");

export function asyncIt<T>(msg: string, v: Thenable<T>, c: (m: jasmine.Matchers<T>) => void) {
  it(msg, (done) => {
    v.tnr(r => c(expect(r)));
    done();
  });
}

export function isA(t: any): any {
  return jasmine.any(t) as any;
}

interface EventTarget {
  value: string;
}

interface ChangeEvent {
  preventDefault: () => void;
  target: EventTarget;
}

export function changeEvent(value: string): ChangeEvent {
  return {
    preventDefault: () => {},
    target: {value: value}
  };
}

export function yieldRedraw(check: () => void) {
  setTimeout(check, 0);
}
