import { BasicFestInfo } from 'app/page/festival/basic-festival-info';


export interface NewFestival {
  basic: BasicFestInfo;
}

export type Fid = number;
export type Uid = number;

export type FestState = 'Announce' |  'Open' |  'Close';

export const Announce: FestState = 'Announce';
export const Open: FestState = 'Open';
export const Close: FestState = 'Close';

export interface FestInfo {
  name: string;
  opensAt: string;
  state: FestState;
}
