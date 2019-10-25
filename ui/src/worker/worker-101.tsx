import {localDateYmd, roundToMinutes} from "util/my-time";

self.addEventListener('install', (event) => {
  let ts = new Date();
  console.log(`Start install worker 101. Version 2. ${roundToMinutes(localDateYmd(ts))}`);
});
