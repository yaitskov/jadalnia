import { h } from 'preact';
import { shallow } from 'preact-render-spy';
import { Wm } from './will-mount';


describe('Will mount component', () => {
  it('callback is called', () => {
    const calls: number[] = [];
    const dom = shallow(<Wm c={() => calls.push(1)} />);
    expect(dom.find('b').length).toBe(1);
    expect(calls).toEqual([1]);
  });
});
