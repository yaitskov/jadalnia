import { time2Str } from 'util/my-time';


export interface BasicFestInfo {
  name: string;
  startAt: string;
  description: string;
}

export const newBasicFestInfo = () => ({
  name: '',
  startAt: time2Str(new Date()),
  description: ''
});
