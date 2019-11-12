import { localDateYmd } from 'util/my-time';
import { uuidV4 } from 'util/crypto';

export interface BasicFestInfo {
  name: string;
  userName: string;
  opensAt: string;
  userKey: string;
}

export const newBasicFestInfo = () => ({
  name: '',
  opensAt: localDateYmd(new Date()) + ' 11:00',
  userName: '',
  userKey: uuidV4()
});
