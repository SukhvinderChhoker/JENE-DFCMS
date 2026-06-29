export interface Evidence {
  id: number;
  reference: string;
  type: string;
  caseId?: number;
  caseName?: string;
  qrCode: boolean;
  comment?: string;
  originator?: string;
  originatorUnit?: string;
  evidenceBagNumber?: string;
  location: string;
  currentStatus: string;
  dateAdded: string;
  photoUrl?: string;
  photoFileName?: string;

  dateOfInduction?: string;
  makeModelNo?: string;
  manufacturerName?: string;
  serialNumber?: string;
  deviceLocked?: boolean;
  depositorName?: string;
  depositorContact?: string;
  evidenceDescription?: string;
  osType?: string;
  storageCapacity?: string;
  conditionAtReceipt?: string;
  sealedStatus?: boolean;
}

export interface ChainOfCustody {
  id: number;
  evidenceId: number;
  userFullname: string;
  custodian: string;
  dateRecorded: string;
  dateOfCustody: string;
  checkIn: boolean;
  comment?: string;
}
