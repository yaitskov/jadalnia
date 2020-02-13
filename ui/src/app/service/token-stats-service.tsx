import {Fid} from "app/page/festival/festival-types";
import {RestSr} from "app/service/rest-service";
import {Thenable} from 'async/abortable-promise';
import { TokenStatsResponse } from "app/types/token";


export class TokenStatsSr {
  // @ts-ignore
  private $restSr: RestSr;

  tokenStats(fid: Fid): Thenable<TokenStatsResponse> {
    return this.$restSr.geT(`/api/token-stats/${fid}`);
  }
}
