import { h } from 'preact';
import { MyCo } from "component/my-component";
import Copy from 'component/icons/copy/copy.svgc';

export class CopyCo extends MyCo<{}, {}> {
  render() {
    return <Copy class="svg-icon" />
  }
}
