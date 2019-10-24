

export const loadServiceWorkers = () => window.addEventListener('load', () => {
  navigator.serviceWorker.register('/worker-101.js')
    .then(registration => {
      console.info(`Service worker is registered ${registration}`)
    }, err => {
      console.error(`Failed to register service worker ${err}`)
    });
});
