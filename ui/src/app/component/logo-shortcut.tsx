import {h} from "preact";
import {MyCo} from "component/my-component";

export class FF extends MyCo<{}, {}> {
  render() {
    return <span>FoodFest</span>;
  }
}
