import { h } from 'preact';

import bulma from 'app/style/my-bulma.sass';

import { T } from 'i18n/translate-tag';
import { TransCom, TransComS } from 'i18n/trans-component';
import {Fid} from 'app/page/festival/festival-types';
import {RestErrCo} from "component/err/error";
import {OrderSr} from "app/service/order-service";
import {OrderLabel, OrderProgress} from "app/types/order";
import {jne} from "collection/join-non-empty";
import {Link} from "preact-router";

export interface OrderProgressViewP {
  fid: Fid;
  ordLbl: OrderLabel;
}

export interface OrderProgressViewS extends TransComS {
  progress?: OrderProgress;
  e?: Error;
}

export class OrderProgressView extends TransCom<OrderProgressViewP, OrderProgressViewS> {
  // @ts-ignore
  private $orderSr: OrderSr;

  constructor(props) {
    super(props);
    this.st = {at: this.at()};
    this.rescheduleOrder = this.rescheduleOrder.bind(this);
    this.pickUpOrder = this.pickUpOrder.bind(this);
  }

  wMnt() {
    this.$orderSr.seeOrderProgress(this.pr.fid, this.pr.ordLbl)
      .tn(progress => this.ust(st => ({...st, progress: progress})))
      .ctch(e => this.ust(st => ({...st, e: e})))
  }

  rescheduleOrder() {
    this.$orderSr.rescheduleOrder(this.pr.ordLbl)
      .tn(outcome => this.$orderSr.seeOrderProgress(this.pr.fid, this.pr.ordLbl))
      .tn(progress => this.ust(st => ({...st, progress: progress})))
      .ctch(e => this.ust(st => ({...st, e: e})))
  }

  pickUpOrder() {
    this.$orderSr.customerPicksOrder(this.pr.ordLbl)
      .tn(outcome => this.$orderSr.seeOrderProgress(this.pr.fid, this.pr.ordLbl))
      .tn(progress => this.ust(st => ({...st, progress: progress})))
      .ctch(e => this.ust(st => ({...st, e: e})))
  }

  render(p, st) {
    const TI = this.c(T);
    return <div>
      {!st.progress && !st.e && <TI m="loading order progress..." o={p.ordLbl}/>}
      <RestErrCo e={st.e} />
      {!!st.progress && <div>
        {st.progress.state == 'Accepted' && <p>
          <TI m="Order o is accepted." o={p.ordLbl}/>
          <TI m="You have to pay to put the order in line."/>
          <Link href={`/festival/visitor/order/autopay/${p.fid}/${p.ordLbl}`}
                class={jne(bulma.button, bulma.isPrimary)}>
            <TI m="pay for the order" />
          </Link>
        </p>}
        {st.progress.state == 'Paid' && <p>
          <TI m="Order o is xth in line" xth={st.progress.ordersAhead} o={p.ordLbl}/>
          <TI m="Estimated order time minutes" time={Math.round(st.progress.etaSeconds / 60)} />
        </p>}
        {st.progress.state == 'Executing' && <p>
          <TI m="Order o is executing" o={p.ordLbl}/>
        </p>}
        {st.progress.state == 'Ready' && <p>
          <TI m="Order o is ready for pickup" o={p.ordLbl}/>
          <br/>
          <button class={jne(bulma.button, bulma.isPrimary)}
                  onClick={this.pickUpOrder}>
            <TI m="I got order" />
          </button>
        </p>}
        {st.progress.state == 'Abandoned' && <p>
          <TI m="Nobody showed to pick order o." o={p.ordLbl}/>
          <TI m="It was marked as abandoned." />
          <TI m="Press reschedule button to put order in line." />
          <button class={jne(bulma.button, bulma.isPrimary)}
                  onClick={this.rescheduleOrder}>
            <TI m="Reschedule order" />
          </button>
        </p>}
        {st.progress.state == 'Cancelled' && <p>
          <TI m="Order o is cancelled" o={p.ordLbl}/>
        </p>}
        {st.progress.state == 'Handed' && <p>
          <TI m="Order o is taken" o={p.ordLbl}/>
        </p>}
      </div>
      }
    </div>;
  }

  at(): string[] { return []; }
}
