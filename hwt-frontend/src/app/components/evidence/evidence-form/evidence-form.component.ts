import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EvidenceService } from '../../../services/evidence.service';
import { CaseService } from '../../../services/case.service';
import { Case } from '../../../models/case.model';

@Component({
  selector: 'app-evidence-form',
  templateUrl: './evidence-form.component.html',
  styleUrls: ['./evidence-form.component.css']
})
export class EvidenceFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  evidenceId: number = 0;
  cases: Case[] = [];
  submitting = false;

  evidenceTypes = ['PC', 'Laptop', 'Ext HDD/SSD', 'Mobile', 'Smart Device', 'OM'];
  originators = ['Army', 'Navy', 'AirForce', 'HQ IDS', 'Others'];
  osTypes = ['Windows', 'Linux', 'macOS', 'Android', 'iOS', 'Other', 'N/A'];
  conditionOptions = ['Good', 'Fair', 'Poor', 'Damaged', 'Scratched', 'Water Damaged', 'Cracked'];
  yesNoOptions = ['Yes', 'No'];

  showOriginatorOther = false;

  selectedPhoto: File | null = null;
  photoPreview: string | null = null;
  selectedDocuments: File[] = [];
  documentNote = '';
  uploading = false;
  uploadedDocuments: any[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private evidenceService: EvidenceService,
    private caseService: CaseService
  ) {
    this.form = this.fb.group({
      reference: ['', Validators.required],
      type: ['PC', Validators.required],
      comment: [''],
      originator: [''],
      originatorUnit: [''],
      evidenceBagNumber: [''],
      location: ['', Validators.required],
      caseId: [''],
      dateOfInduction: [''],
      makeModelNo: [''],
      manufacturerName: [''],
      serialNumber: [''],
      deviceLocked: [''],
      depositorName: [''],
      depositorContact: [''],
      evidenceDescription: [''],
      osType: [''],
      storageCapacity: [''],
      conditionAtReceipt: [''],
      sealedStatus: ['']
    });
  }

  ngOnInit(): void {
    this.loadCases();
    const caseId = this.route.snapshot.queryParamMap.get('caseId');
    if (caseId) {
      this.form.patchValue({ caseId: Number(caseId) });
    }

    if (this.router.url.includes('/evidence/') && this.router.url.includes('/edit')) {
      this.isEdit = true;
      this.evidenceId = Number(this.route.snapshot.paramMap.get('id'));
      this.loadEvidence();
      this.loadDocuments();
    }

    this.form.get('originator')?.valueChanges.subscribe(val => {
      this.showOriginatorOther = val === 'Others';
    });
  }

  loadCases(): void {
    this.caseService.getAll().subscribe({
      next: (data) => this.cases = data
    });
  }

  loadEvidence(): void {
    this.evidenceService.getById(this.evidenceId).subscribe({
      next: (data) => {
        this.form.patchValue({
          reference: data.reference,
          type: data.type,
          comment: data.comment,
          originator: data.originator,
          originatorUnit: data.originatorUnit,
          evidenceBagNumber: data.evidenceBagNumber,
          location: data.location,
          caseId: data.caseId,
          dateOfInduction: data.dateOfInduction || '',
          makeModelNo: data.makeModelNo,
          manufacturerName: data.manufacturerName,
          serialNumber: data.serialNumber,
          deviceLocked: data.deviceLocked !== null && data.deviceLocked !== undefined ? (data.deviceLocked ? 'Yes' : 'No') : '',
          depositorName: data.depositorName,
          depositorContact: data.depositorContact,
          evidenceDescription: data.evidenceDescription,
          osType: data.osType,
          storageCapacity: data.storageCapacity,
          conditionAtReceipt: data.conditionAtReceipt,
          sealedStatus: data.sealedStatus !== null && data.sealedStatus !== undefined ? (data.sealedStatus ? 'Yes' : 'No') : ''
        });
        if (data.originator === 'Others') {
          this.showOriginatorOther = true;
        }
        if (data.photoUrl) {
          this.photoPreview = 'http://localhost:8080' + data.photoUrl;
        }
      }
    });
  }

  loadDocuments(): void {
    this.evidenceService.getDocuments(this.evidenceId).subscribe({
      next: (docs) => this.uploadedDocuments = docs
    });
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedPhoto = input.files[0];
      const reader = new FileReader();
      reader.onload = () => this.photoPreview = reader.result as string;
      reader.readAsDataURL(this.selectedPhoto);
    }
  }

  removePhoto(): void {
    this.selectedPhoto = null;
    this.photoPreview = null;
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
      const data: any = { ...this.form.value };

      if (data.deviceLocked === 'Yes') data.deviceLocked = true;
      else if (data.deviceLocked === 'No') data.deviceLocked = false;
      else data.deviceLocked = null;

      if (data.sealedStatus === 'Yes') data.sealedStatus = true;
      else if (data.sealedStatus === 'No') data.sealedStatus = false;
      else data.sealedStatus = null;

      if (this.isEdit) {
        this.evidenceService.update(this.evidenceId, data).subscribe({
          next: () => this.uploadFilesAndNavigate(this.evidenceId),
          error: () => this.submitting = false
        });
      } else {
        const caseId = data.caseId || 0;
        this.evidenceService.create(caseId, data).subscribe({
          next: (created) => this.uploadFilesAndNavigate(created.id),
          error: () => this.submitting = false
        });
      }
    }
  }

  uploadFilesAndNavigate(evidenceId: number): void {
    let pending = 0;
    let done = 0;

    const checkDone = () => {
      done++;
      if (done >= pending) {
        this.router.navigate(['/evidence', evidenceId]);
      }
    };

    if (this.selectedPhoto) {
      pending++;
      this.evidenceService.uploadPhoto(evidenceId, this.selectedPhoto).subscribe({
        next: () => checkDone(),
        error: () => checkDone()
      });
    }

    this.selectedDocuments.forEach((file) => {
      pending++;
      this.evidenceService.uploadDocument(evidenceId, file, this.documentNote).subscribe({
        next: () => checkDone(),
        error: () => checkDone()
      });
    });

    if (pending === 0) {
      this.router.navigate(['/evidence', evidenceId]);
    }
  }
}
