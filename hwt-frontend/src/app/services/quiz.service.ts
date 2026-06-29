import { Injectable } from '@angular/core';
import { QuizQuestion, QuizResult, QUIZ_CATEGORIES, QUIZ_LEVELS } from '../models/quiz.model';
import { QUIZ_QUESTIONS } from '../data/quiz-questions';

@Injectable({ providedIn: 'root' })
export class QuizService {
  private questions = QUIZ_QUESTIONS;
  private history: QuizResult[] = [];

  constructor() {
    const stored = localStorage.getItem('quizHistory');
    if (stored) {
      this.history = JSON.parse(stored);
    }
  }

  getCategories() {
    return QUIZ_CATEGORIES;
  }

  getLevels() {
    return QUIZ_LEVELS;
  }

  getQuestionsForQuiz(categoryId: string, levelId: string, count: number = 15): QuizQuestion[] {
    const cat = this.questions[categoryId];
    if (!cat) return [];
    const level = cat[levelId];
    if (!level || level.length === 0) return [];
    const shuffled = [...level].sort(() => Math.random() - 0.5);
    return shuffled.slice(0, Math.min(count, shuffled.length));
  }

  getTotalQuestions(categoryId: string, levelId: string): number {
    const cat = this.questions[categoryId];
    if (!cat) return 0;
    return cat[levelId]?.length || 0;
  }

  calculateResult(categoryId: string, levelId: string, answers: { question: QuizQuestion; selectedIndex: number }[]): QuizResult {
    let correct = 0;
    const detailedAnswers = answers.map(a => {
      const isCorrect = a.selectedIndex === a.question.correctIndex;
      if (isCorrect) correct++;
      return { ...a, correct: isCorrect };
    });

    const total = answers.length;
    const score = Math.round((correct / total) * 100);
    const passed = score >= 70;

    const result: QuizResult = {
      category: categoryId,
      level: levelId,
      totalQuestions: total,
      correctAnswers: correct,
      score,
      passed,
      answers: detailedAnswers,
      completedAt: new Date()
    };

    this.history.push(result);
    localStorage.setItem('quizHistory', JSON.stringify(this.history));
    return result;
  }

  getHistory(): QuizResult[] {
    return this.history;
  }

  getCategoryName(id: string): string {
    return QUIZ_CATEGORIES.find(c => c.id === id)?.name || id;
  }

  getLevelName(id: string): string {
    return QUIZ_LEVELS.find(l => l.id === id)?.name || id;
  }

  getLevelColor(id: string): string {
    return QUIZ_LEVELS.find(l => l.id === id)?.color || '#666';
  }

  getStats() {
    const totalQuizzes = this.history.length;
    const totalCorrect = this.history.reduce((sum, r) => sum + r.correctAnswers, 0);
    const totalQuestions = this.history.reduce((sum, r) => sum + r.totalQuestions, 0);
    const avgScore = totalQuizzes > 0 ? Math.round(this.history.reduce((sum, r) => sum + r.score, 0) / totalQuizzes) : 0;
    const passed = this.history.filter(r => r.passed).length;

    const categoryStats: Record<string, { taken: number, avgScore: number, bestScore: number }> = {};
    this.history.forEach(r => {
      if (!categoryStats[r.category]) {
        categoryStats[r.category] = { taken: 0, avgScore: 0, bestScore: 0 };
      }
      categoryStats[r.category].taken++;
      categoryStats[r.category].avgScore = Math.round(
        (categoryStats[r.category].avgScore * (categoryStats[r.category].taken - 1) + r.score) / categoryStats[r.category].taken
      );
      categoryStats[r.category].bestScore = Math.max(categoryStats[r.category].bestScore, r.score);
    });

    return { totalQuizzes, totalCorrect, totalQuestions, avgScore, passed, categoryStats };
  }
}
