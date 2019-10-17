import { h } from 'preact';
import { shallow } from 'preact-render-spy';
import { If, IfSt } from './if';

describe('If component', () => {
  it('display is block when visible', () =>
    expect(shallow(<If f={true} />).find('div').at(0).attrs())
      .toEqual({style: {display: "block"}}));
  it('display is none when invisible', () =>
    expect(shallow(<If f={false} />).find('div').at(0).attrs())
      .toEqual({style: {display: "none"}}));
});
