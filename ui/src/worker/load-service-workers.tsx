// function sendSubscriptionToServer(subscription) {
//   let key = subscription.getKey ? subscription.getKey('p256dh') : '';
//   let auth = subscription.getKey ? subscription.getKey('auth') : '';
//
//   console.info(`Send key ${key} and auth ${auth} to server`);
// }
//
// function subscribe() {
//   console.log('lets subscribe');
//   navigator.serviceWorker.ready.then(serviceWorkerRegistration => {
//     console.log('sw ready for making subscribe');
//     serviceWorkerRegistration.pushManager
//       .subscribe({userVisibleOnly: true})
//       .then(
//         subscription => {
//           console.log(`have subscription ${subscription}`);
//           return sendSubscriptionToServer(subscription)
//         })
//       .catch(e => {
//         if (Notification.permission == 'denied') {
//           console.warn('Permission for notification was denied');
//         } else {
//           console.log(`Unable to subscribe to push ${e}`);
//         }
//       });
//   }).catch(e => {
//     console.info(`sw is not ready for subscribe due ${e}`);
//   });
// }

export const loadServiceWorkers = () => window.addEventListener('load', () => {
  navigator.serviceWorker.register('/sw.bundle.js')
    .then(
      registration => {
        console.info(`Service worker is registered ${registration}`)

        // if (!('showNotification' in ServiceWorkerRegistration.prototype)) {
        //   console.warn("notifications are not supported");
        //   return;
        // }
        //
        // if (Notification.permission  == 'denied') {
        //   console.warn("Notifications are disabled");
        //   return;
        // }
        //
        // if (typeof PushManager == "undefined") {
        //   console.warn("Push messaging is not supported");
        //   return;
        // }
        //
        // navigator.serviceWorker.ready.then(
        //   serviceWorkerRegistration => {
        //     console.log(`sw ready`);
        //     serviceWorkerRegistration.pushManager.getSubscription()
        //       .then(subscription => {
        //         if (!subscription) {
        //           subscribe();
        //           return;
        //         }
        //         sendSubscriptionToServer(subscription);
        //       }).catch(e => console.info(`Error during subscription ${e}`))
        //   }).catch(e => {
        //   console.info(`sw not ready ${e}`);
        // });

      }, err => {
        console.info(`Failed to register service worker ${err}`)
      });
});
