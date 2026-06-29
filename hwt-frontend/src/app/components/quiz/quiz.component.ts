import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { QuizCategory, QuizLevel, QuizQuestion } from '../../models/quiz.model';

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.css']
})
export class QuizComponent implements OnInit {
  categories: QuizCategory[] = [];
  levels: QuizLevel[] = [];

  selectedCategory = '';
  selectedLevel = '';
  currentQuestionIndex = 0;
  questions: QuizQuestion[] = [];
  answers: { question: QuizQuestion; selectedIndex: number }[] = [];
  selectedOption: number = -1;
  quizStarted = false;
  quizCompleted = false;
  questionCount = 15;

  constructor(private quizService: QuizService, private router: Router) {}

  ngOnInit(): void {
    this.categories = this.quizService.getCategories();
    this.levels = this.quizService.getLevels();
  }

  getCategoryQuestionCount(catId: string, levelId: string): number {
    return this.quizService.getTotalQuestions(catId, levelId);
  }

  canStartQuiz(): boolean {
    return this.selectedCategory !== '' && this.selectedLevel !== '';
  }

  startQuiz(): void {
    if (!this.canStartQuiz()) return;
    this.questions = this.quizService.getQuestionsForQuiz(this.selectedCategory, this.selectedLevel, this.questionCount);
    if (this.questions.length === 0) return;
    this.currentQuestionIndex = 0;
    this.answers = [];
    this.selectedOption = -1;
    this.quizStarted = true;
    this.quizCompleted = false;
  }

  get currentQuestion(): QuizQuestion | null {
    return this.questions[this.currentQuestionIndex] || null;
  }

  get progress(): number {
    return this.questions.length > 0 ? ((this.currentQuestionIndex) / this.questions.length) * 100 : 0;
  }

  selectOption(index: number): void {
    this.selectedOption = index;
  }

  nextQuestion(): void {
    if (this.selectedOption === -1) return;
    this.answers.push({
      question: this.questions[this.currentQuestionIndex],
      selectedIndex: this.selectedOption
    });
    this.selectedOption = -1;
    this.currentQuestionIndex++;
    if (this.currentQuestionIndex >= this.questions.length) {
      this.finishQuiz();
    }
  }

  finishQuiz(): void {
    const result = this.quizService.calculateResult(this.selectedCategory, this.selectedLevel, this.answers);
    sessionStorage.setItem('lastQuizResult', JSON.stringify(result));
    this.quizCompleted = true;
    this.router.navigate(['/quiz/result']);
  }

  goBack(): void {
    this.quizStarted = false;
    this.quizCompleted = false;
    this.selectedCategory = '';
    this.selectedLevel = '';
    this.questions = [];
    this.answers = [];
  }

  getLevelColor(levelId: string): string {
    return this.quizService.getLevelColor(levelId);
  }

  getTotalForCategory(catId: string): number {
    let total = 0;
    this.levels.forEach(l => total += this.getCategoryQuestionCount(catId, l.id));
    return total;
  }
}
