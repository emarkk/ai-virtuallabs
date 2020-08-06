import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject, Observable, of } from 'rxjs';
import { first, switchMap } from 'rxjs/operators';

import { VmSignal } from '../models/signals/vm.signal';
import { TeamVmsResourcesSignal } from '../models/signals/team-vms-resources.signal';

import { serverUrl } from '../utils';

declare var SockJS;
declare var Stomp;

export class SignalObservable<T> {
  private _subject: Subject<T>;
  private _subscription: any;

  constructor(onSignal, mapMessage) {
    this._subject = new Subject<T>();
    this._subscription = onSignal(msg => this._subject.next(mapMessage(JSON.parse(msg.body))));
  }
  data(): Observable<T> {
    return this._subject.asObservable();
  }
  unsubscribe(): void {
    this._subscription.unsubscribe();
  }
}

@Injectable({
  providedIn: 'root'
})
export class SignalService {
  private token: string;
  private stompClient;
  private connected = new BehaviorSubject<boolean>(false);
  
  constructor() {
  }

  initWebsocket(token: string) {
    if(this.stompClient)
      return;

    this.token = token;
    const ws = new SockJS(serverUrl + 'signals');
    this.stompClient = Stomp.over(ws);
    this.stompClient.connect({ token }, () => {
      this.connected.next(true);
    });
  }
  releaseWebsocket() {
    if(!this.stompClient)
      return;

    this.token = null;
    this.stompClient.disconnect(() => {
      this.stompClient = null;
    });
    this.connected.next(false);
  }

  private _signal(url, map): Observable<SignalObservable<any>> {
    return this.connected.pipe(
      first(c => c),
      switchMap(() => of(new SignalObservable(cb => this.stompClient.subscribe(url, cb, { token: this.token }), map)))
    );
  }

  vmUpdates(vmId: number): Observable<SignalObservable<VmSignal>> {
    return this._signal(`/vm/${vmId}`, msg => VmSignal.fromMsg(msg));
  }
  vmScreenUpdates(vmId: number): Observable<SignalObservable<any>> {
    return this._signal(`/vm/${vmId}/screen`, msg => null);
  }
  teamVmsUpdates(teamId: number): Observable<SignalObservable<VmSignal>> {
    return this._signal(`/team/${teamId}/vms`, msg => VmSignal.fromMsg(msg));
  }
  teamVmsLimitsUpdates(teamId: number): Observable<SignalObservable<TeamVmsResourcesSignal>> {
    return this._signal(`/team/${teamId}/vms-resources`, msg => TeamVmsResourcesSignal.fromMsg(msg));
  }
  courseVmsUpdates(courseCode: string): Observable<SignalObservable<VmSignal>> {
    return this._signal(`/course/${courseCode}/vms`, msg => VmSignal.fromMsg(msg));
  }
}