import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { QuizResult } from '../../models/quiz.model';

@Component({
  selector: 'app-quiz-result',
  templateUrl: './quiz-result.component.html',
  styleUrls: ['./quiz-result.component.css']
})
export class QuizResultComponent implements OnInit {
  result: QuizResult | null = null;
  showExplanations = false;

  constructor(private router: Router, private quizService: QuizService) {}

  ngOnInit(): void {
    const stored = sessionStorage.getItem('lastQuizResult');
    if (stored) {
      this.result = JSON.parse(stored);
    }
  }

  get categoryName(): string {
    return this.result ? this.quizService.getCategoryName(this.result.category) : '';
  }

  get levelName(): string {
    return this.result ? this.quizService.getLevelName(this.result.level) : '';
  }

  get levelColor(): string {
    return this.result ? this.quizService.getLevelColor(this.result.level) : '#666';
  }

  get scoreColor(): string {
    if (!this.result) return '#666';
    if (this.result.score >= 90) return '#4caf50';
    if (this.result.score >= 70) return '#ff9800';
    return '#f44336';
  }

  toggleExplanations(): void {
    this.showExplanations = !this.showExplanations;
  }

  retryQuiz(): void {
    this.router.navigate(['/quiz']);
  }

  goHome(): void {
    this.router.navigate(['/dashboard']);
  }
}
