type StringNullOrUndef = string | null | undefined | boolean;

export const jne = (...parts: StringNullOrUndef[]): string => parts.filter(p => p).join(' ');
