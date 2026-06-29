export interface Task {
  id: number;
  taskName: string;
  caseId: number;
  caseName: string;
  taskType: string;
  background?: string;
  currentStatus: string;
  location?: string;
  creationDate: string;
  deadline?: string;
  principleInvestigator?: string;
  secondaryInvestigator?: string;
  principleQA?: string;
  secondaryQA?: string;
}

export interface TaskNote {
  id: number;
  note: string;
  authorFullname: string;
  date_time: string;
  hash: string;
}
