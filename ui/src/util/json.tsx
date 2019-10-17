export const o2j = <T extends {} >(o: T): string => JSON.stringify(o);
export const j2o = <T extends {} >(o: string): T => JSON.parse(o);
