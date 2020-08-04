import { TeamVmsResources } from '../team-vms-resources.model';

export enum TeamVmsResourcesSignalUpdateType {
  USED = 'USED',
  TOTAL = 'TOTAL'
};

export class TeamVmsResourcesSignal {
  vmsResources: TeamVmsResources;
  updateType: TeamVmsResourcesSignalUpdateType;

  constructor(vmsResources: TeamVmsResources, updateType: TeamVmsResourcesSignalUpdateType) {
    this.vmsResources = vmsResources;
    this.updateType = updateType;
  }
  static fromMsg(msg: any): TeamVmsResourcesSignal {
    return null;
  }
};