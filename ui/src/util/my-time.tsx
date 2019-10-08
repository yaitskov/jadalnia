import parseISO from 'date-fns/parseISO/index';

export const isoTime = (s: string): Date => parseISO(s);
export const time2Str = (d: Date): string => d.toISOString();
