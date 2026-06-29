import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CaseService } from '../../../services/case.service';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-case-form',
  templateUrl: './case-form.component.html',
  styleUrls: ['./case-form.component.css']
})
export class CaseFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  caseId: number = 0;
  users: User[] = [];
  submitting = false;

  classifications = ['Unclassified', 'Official', 'Secret', 'Top Secret'];
  caseTypes = ['Investigation', 'Enquiry', 'Incident', 'Other'];
  priorities = ['Low', 'Normal', 'High', 'Critical'];

  selectedDocuments: File[] = [];
  documentNote = '';
  uploadedDocuments: any[] = [];

  documentChecklist = [
    { key: 'cocReceived', label: 'Chain of Custody (COC) Form', checked: false },
    { key: 'agencyLetterReceived', label: 'Letter from Investigating Agency', checked: false },
    { key: 'authorityLetterReceived', label: 'Authority Letter', checked: false },
    { key: 'consentFormReceived', label: 'User Consent Form', checked: false },
    { key: 'caseHistoryReceived', label: 'History of Case', checked: false },
    { key: 'handlingTakingFormReceived', label: 'Handling / Taking Over Form', checked: false },
    { key: 'evidencePhotosReceived', label: 'Photographs of Evidence', checked: false },
    { key: 'seizureMemoReceived', label: 'Seizure Memo', checked: false },
    { key: 'witnessStatementReceived', label: 'Witness Statement', checked: false },
    { key: 'otherDocumentsReceived', label: 'Other Supporting Documents', checked: false }
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private caseService: CaseService,
    private userService: UserService,
    private authService: AuthService
  ) {
    this.form = this.fb.group({
      caseName: ['', Validators.required],
      reference: [''],
      background: [''],
      location: [''],
      classification: ['Unclassified'],
      caseType: ['Investigation'],
      casePriority: ['Normal', Validators.required],
      privateCase: [false],
      deadline: [''],
      authoriser: [''],
      justification: ['']
    });
  }

  ngOnInit(): void {
    this.loadUsers();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.caseId = Number(id);
      this.loadCase();
    }
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (data) => this.users = data
    });
  }

  loadCase(): void {
    this.caseService.getById(this.caseId).subscribe({
      next: (data) => {
        this.form.patchValue({
          caseName: data.caseName,
          reference: data.reference,
          background: data.background,
          location: data.location,
          classification: data.classification,
          caseType: data.caseType,
          casePriority: data.casePriority,
          privateCase: data.privateCase,
          deadline: data.deadline,
          authoriser: data.authoriser,
          justification: data.justification
        });
        if ((data as any).documentChecklist) {
          const cl = (data as any).documentChecklist;
          this.documentChecklist.forEach(item => {
            item.checked = cl[item.key] === true;
          });
        }
      }
    });
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.caseService.getDocuments(this.caseId).subscribe({
      next: (docs) => this.uploadedDocuments = docs
    });
  }

  onDocumentsSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      for (let i = 0; i < input.files.length; i++) {
        this.selectedDocuments.push(input.files[i]);
      }
    }
    input.value = '';
  }

  removeDocument(index: number): void {
    this.selectedDocuments.splice(index, 1);
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.submitting = true;
      const data = this.form.value;

      const checklist: any = {};
      this.documentChecklist.forEach(item => {
        checklist[item.key] = item.checked;
      });
      (data as any).documentChecklist = checklist;

      if (data.deadline && typeof data.deadline === 'string' && data.deadline.includes('T')) {
        const d = new Date(data.deadline);
        const pad = (n: number) => n.toString().padStart(2, '0');
        data.deadline = `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
      }

      if (this.isEdit) {
        this.caseService.update(this.caseId, data).subscribe({
          next: () => this.uploadDocsAndNavigate(this.caseId),
          error: () => this.submitting = false
        });
      } else {
        this.caseService.create(data).subscribe({
          next: (created) => this.uploadDocsAndNavigate(created.id),
          error: () => this.submitting = false
        });
      }
    }
  }

  uploadDocsAndNavigate(caseId: number): void {
    if (this.selectedDocuments.length === 0) {
      this.router.navigate(['/cases', caseId]);
      return;
    }
    let done = 0;
    this.selectedDocuments.forEach((file) => {
      this.caseService.uploadDocument(caseId, file, this.documentNote).subscribe({
        next: () => { done++; if (done >= this.selectedDocuments.length) this.router.navigate(['/cases', caseId]); },
        error: () => { done++; if (done >= this.selectedDocuments.length) this.router.navigate(['/cases', caseId]); }
      });
    });
  }
}
