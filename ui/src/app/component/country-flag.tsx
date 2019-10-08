import { h } from 'preact';
import { MyCo } from 'component/my-component';
import { LanguageCode } from 'i18n/i18n-translator';

import flagIcon from 'flag-icon-css/css/flag-icon.css';

export interface CountryFlagP {
  code: LanguageCode;
}

const ex = {en: 'us'};

export class CountryFlag extends MyCo<CountryFlagP, {}> {
  render() {
    return <span class={flagIcon.flagIcon + ' ' + flagIcon[`flag-icon-${ex[this.props.code] || this.props.code}`]}/>;
  }
}
