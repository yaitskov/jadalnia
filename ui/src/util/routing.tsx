export const goBack = () => window.history.go(-1);
export const siteUrl = () => location.href.substring(0, location.href.indexOf('/', 10));
export const isAbsUrl = (url: string) => url.match(/^http/);
