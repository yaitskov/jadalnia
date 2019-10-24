import { render, h, Component } from 'preact';
import { inject } from 'injection/inject-1k';
import { container } from 'app/main-context';
import { MainCom } from 'app/main-component';
import { loadServiceWorkers } from "worker/load-service-workers";

loadServiceWorkers();

const MainComI = inject(MainCom, container);

render(<MainComI/>, document.body);
