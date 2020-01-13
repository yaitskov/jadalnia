import parseISO from 'date-fns/parseISO/index';

const pad2 = (n: Number): string => {
  let s = `00${n}`;
  return s.substring(s.length - 2, s.length);
}

export const isoTime = (s: string): Date => parseISO(s);
export const time2Str = (d: Date): string => d.toISOString();
export const localDateYmd = (d: Date): string =>
  `${d.getFullYear()}-${pad2(d.getMonth()+1)}-${pad2(d.getDate())}`;

export const localTimeHm = (d: Date): string => `${d.getHours()}:${pad2(d.getMinutes())}`;

export const roundToMinutes = (s: string): string =>
  s.replace(/T/, ' ').replace(/[:][0-9]{2}[.][0-9]{3}Z$/, '');
