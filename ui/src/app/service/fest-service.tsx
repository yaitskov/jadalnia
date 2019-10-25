import { Thenable } from 'async/abortable-promise';
import { Fid, FestState } from 'app/page/festival/festival-types';
import { RestSr } from "app/service/rest-service";

export class FestSr {
  // @ts-ignore
  private $restSr: RestSr;

  getState(fid: Fid): Thenable<FestState> {
    return this.$restSr.geT(`/api/festival/state/${fid}`);
  }

  setState(fid: Fid, newState: FestState): Thenable<boolean> {
    return this.$restSr.postJ(`/api/festival/state/fid/${newState}`, []);
  }
}
