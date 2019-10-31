import {Thenable, resolved, ofPromise, boom} from "async/abortable-promise";


function sendSubscriptionToServer(subscription) {
  let key = subscription.getKey ? subscription.getKey('p256dh') : '';
  let auth = subscription.getKey ? subscription.getKey('auth') : '';

  console.info(`Send key ${key} and auth ${auth} to server`);
}

function subscribe() {
  console.log('lets subscribe');
  navigator.serviceWorker.ready.then(serviceWorkerRegistration => {
    console.log('sw ready for making subscribe');
    serviceWorkerRegistration.pushManager
      .subscribe({
        userVisibleOnly: true,
        applicationServerKey: "BA5wU2icgNXzmjTG5f_ba0tliY9OezaKETqplE7uoe16sF258jTIDJFkHeLLoCpwNsKiNMoFNEK2Z5Yc-zKyTFY"})
      .then(
        subscription => {
          console.log(`have subscription ${subscription}`);
          return sendSubscriptionToServer(subscription)
        })
      .catch(e => {
        if (Notification.permission == 'denied') {
          console.warn('Permission for notification was denied');
        } else {
          console.log(`Unable to subscribe to push ${e}`);
        }
      });
  }).catch(e => {
    console.info(`sw is not ready for subscribe due ${e}`);
  });
}

export class PushSr {
  isSupported(): boolean {
    return typeof Notification != 'undefined'
      && typeof ServiceWorkerRegistration != 'undefined'
      && 'showNotification' in ServiceWorkerRegistration.prototype
      && typeof PushManager != "undefined";
  }

  isAllowed(): Thenable<boolean> {
    if (!this.isSupported()) {
      return resolved(false);
    }
    if (Notification.permission != 'granted') {
      return resolved(false);
    }
    return ofPromise(navigator.serviceWorker.ready).tn(
      swr => swr.pushManager.getSubscription().then(subscription => !!subscription));
  }

  askForPerms(): Thenable<boolean> {
    if (!this.isSupported()) {
      return boom(new Error("not supported"));
    }

    return ofPromise(navigator.serviceWorker.ready).tn(
      serviceWorkerRegistration => {
        console.log(`sw ready`);
        return serviceWorkerRegistration.pushManager.getSubscription()
          .then(subscription => {
            if (!subscription) {
              subscribe();
              return;
            }
            sendSubscriptionToServer(subscription);
          }); // .ctch(e => console.info(`Error during subscription ${e}`))
      }).tn(ok => true);// .catch(e => {
      //console.info(`sw not ready ${e}`);
  }
}
