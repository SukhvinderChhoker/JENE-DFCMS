export interface Evidence {
  id: number;
  reference: string;
  type: string;
  caseId?: number;
  caseName?: string;
  qrCode: boolean;
  comment?: string;
  originator?: string;
  evidenceBagNumber?: string;
  location: string;
  currentStatus: string;
  dateAdded: string;
  photoUrl?: string;
  photoFileName?: string;
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
