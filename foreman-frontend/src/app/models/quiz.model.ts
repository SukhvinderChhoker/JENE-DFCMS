export interface QuizQuestion {
  id: number;
  question: string;
  options: string[];
  correctIndex: number;
  explanation: string;
}

export interface QuizCategory {
  id: string;
  name: string;
  icon: string;
  description: string;
}

export interface QuizLevel {
  id: string;
  name: string;
  description: string;
  color: string;
}

export interface QuizResult {
  category: string;
  level: string;
  totalQuestions: number;
  correctAnswers: number;
  score: number;
  passed: boolean;
  answers: { question: QuizQuestion; selectedIndex: number; correct: boolean }[];
  completedAt: Date;
}

export const QUIZ_CATEGORIES: QuizCategory[] = [
  { id: 'windows', name: 'Windows Forensics', icon: 'computer', description: 'Windows OS investigation, registry, event logs, NTFS' },
  { id: 'linux', name: 'Linux Forensics', icon: 'terminal', description: 'Linux system investigation, file systems, logs' },
  { id: 'macos', name: 'macOS Forensics', icon: 'laptop_mac', description: 'Apple Mac investigation, Unified Logs, Keychain' },
  { id: 'android', name: 'Android Forensics', icon: 'phone_android', description: 'Android device investigation, ADB, SQLite' },
  { id: 'ios', name: 'iOS/Apple Forensics', icon: 'phone_iphone', description: 'iPhone/iPad investigation, iTunes backup, Keychain' },
  { id: 'malware', name: 'Malware Analysis', icon: 'bug_report', description: 'Malware reverse engineering, behavior analysis' },
  { id: 'network', name: 'Network Forensics', icon: 'wifi', description: 'Packet capture, traffic analysis, intrusion detection' },
  { id: 'memory', name: 'Memory Forensics', icon: 'sd', description: 'RAM analysis, Volatility, live forensics' },
  { id: 'database', name: 'Database Forensics', icon: 'storage', description: 'SQL investigation, transaction logs, data recovery' },
  { id: 'blockchain', name: 'Blockchain Forensics', icon: 'chain', description: 'Cryptocurrency tracing, transaction analysis' },
  { id: 'cloud', name: 'Cloud Forensics', icon: 'cloud', description: 'Cloud service investigation, SaaS forensics' },
  { id: 'mobile', name: 'Mobile Device Forensics', icon: 'smartphone', description: 'Cross-platform mobile investigation techniques' }
];

export const QUIZ_LEVELS: QuizLevel[] = [
  { id: 'beginner', name: 'Beginner (Fresher)', description: 'Fundamental concepts and basic tools', color: '#4caf50' },
  { id: 'intermediate', name: 'Intermediate', description: 'Practical investigation techniques', color: '#ff9800' },
  { id: 'advanced', name: 'Advanced', description: 'Complex scenarios and advanced tools', color: '#f44336' },
  { id: 'expert', name: 'Expert', description: 'Expert-level analysis and research', color: '#9c27b0' }
];
