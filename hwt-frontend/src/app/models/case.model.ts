export interface Case {
  id: number;
  caseName: string;
  reference?: string;
  currentStatus: string;
  privateCase: boolean;
  background?: string;
  location?: string;
  creationDate: string;
  classification?: string;
  caseType?: string;
  justification?: string;
  casePriority: string;
  casePriorityColour: string;
  deadline?: string;
  taskCount: number;
  evidenceCount: number;
  principleCaseManager?: string;
  secondaryCaseManager?: string;
  requester?: string;
  authoriser?: string;
}

export interface CaseHistory {
  id: number;
  caseName: string;
  userFullname: string;
  date_time: string;
  changes: { [key: string]: [any, any] };
}
