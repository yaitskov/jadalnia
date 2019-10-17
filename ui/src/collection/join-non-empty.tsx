type StringNullOrUndef = string | null | undefined;

export const jne = (...parts: StringNullOrUndef[]): string => parts.filter(p => p).join(' ');
