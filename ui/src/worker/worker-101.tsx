import {localDateYmd, roundToMinutes} from "util/my-time";

self.addEventListener('message', event => {
  console.log(`sw message event ${event}`);
});

self.addEventListener('push', event => {
  console.log(`sw push event ${event}`);

  // show notification
});

self.addEventListener('pushsubscriptionchange', event => {
  console.log(`sw push subscription change event ${event}`);
});

self.addEventListener('sync', event => {
  console.log(`sw sync ${event}`);
});

self.addEventListener('online', event => {
  console.log(`sw online ${event}`);
});

self.addEventListener('offline', event => {
  console.log(`sw offline ${event}`);
});

self.addEventListener('pagehide', event => {
  console.log(`sw pagehide ${event}`);
});

self.addEventListener('pageshow', event => {
  console.log(`sw pageshow ${event}`);
});

self.addEventListener('unload', event => {
  console.log(`sw unload ${event}`);
});

self.addEventListener('activate', event => {
  console.log(`sw activate event ${event}`);
});

self.addEventListener('fetch', event => {
  console.log(`sw fetch event ${event}`);
});

self.addEventListener('install', (event) => {
  let ts = new Date();
  console.log(`Start install worker 101. Version 2. ${roundToMinutes(localDateYmd(ts))}`);

  // init cache
});
