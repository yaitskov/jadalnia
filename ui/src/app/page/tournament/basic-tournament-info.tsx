import { time2Str } from 'util/my-time';

export type SportType = 'TE' | 'TP';

export interface BasicTourInfo {
  name: string;
  type: SportType;
  startAt: string;
  placeId: number;
  placeName: string;
  price: number;
  description: string;
}

export const newBasicTourInfo = (type: SportType) => ({
  name: '',
  type: type,
  startAt: time2Str(new Date()),
  placeId: 0,
  placeName: '',
  price: 0,
  description: ''
});
