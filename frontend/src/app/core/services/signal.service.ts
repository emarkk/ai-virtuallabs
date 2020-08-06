import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject, Observable, of } from 'rxjs';
import { first, switchMap } from 'rxjs/operators';

import { VmSignal } from '../models/signals/vm.signal';
import { VmScreenSignal } from '../models/signals/vm-screen.signal';
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

  private _subscribeTo(url, map): Observable<SignalObservable<any>> {
    return this.connected.pipe(
      first(c => c),
      switchMap(() => of(new SignalObservable(cb => this.stompClient.subscribe(url, cb, { token: this.token }), map)))
    );
  }
  private _sendTo(url, data, token): void {
    this.connected.pipe(
      first(c => c),
      switchMap(() => this.stompClient.send(url, { token }, JSON.stringify(data)))
    );
  }

  vmUpdates(vmId: number): Observable<SignalObservable<VmSignal>> {
    return this._subscribeTo(`/vm/${vmId}`, msg => VmSignal.fromMsg(msg));
  }
  vmScreenUpdates(vmId: number): Observable<SignalObservable<VmScreenSignal>> {
    return this._subscribeTo(`/vm/${vmId}/screen`, msg => VmScreenSignal.fromMsg(msg));
  }
  teamVmsUpdates(teamId: number): Observable<SignalObservable<VmSignal>> {
    return this._subscribeTo(`/team/${teamId}/vms`, msg => VmSignal.fromMsg(msg));
  }
  teamVmsResourcesUpdates(teamId: number): Observable<SignalObservable<TeamVmsResourcesSignal>> {
    return this._subscribeTo(`/team/${teamId}/vms-resources`, msg => TeamVmsResourcesSignal.fromMsg(msg));
  }
  courseVmsUpdates(courseCode: string): Observable<SignalObservable<VmSignal>> {
    return this._subscribeTo(`/course/${courseCode}/vms`, msg => VmSignal.fromMsg(msg));
  }

  sendScreenSignal(vmId: number, token: string, signal: VmScreenSignal): void {
    this._sendTo(`/signal/vm/${vmId}/screen`, signal, token);
  }
}