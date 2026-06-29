import { Component, OnInit } from '@angular/core';
import { ReportService } from '../../services/report.service';
import { CaseService } from '../../services/case.service';
import { TaskService } from '../../services/task.service';
import { DashboardStats } from '../../models/dashboard.model';
import { Case } from '../../models/case.model';
import { Task } from '../../models/task.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalCases: 0,
    openCases: 0,
    closedCases: 0,
    totalTasks: 0,
    queuedTasks: 0,
    completedTasks: 0,
    totalEvidence: 0,
    activeUsers: 0
  };
  recentCases: Case[] = [];
  recentTasks: Task[] = [];
  loading = true;
  dailyTip = '';

  private forensicTips: string[] = [
    'Windows: Always check $MFT (Master File Table) for file activity timestamps - it records creation, modification, and access times even after deletion.',
    'Linux: Examine /var/log/auth.log for authentication attempts and /var/log/syslog for system events during incident timeframe.',
    'Android: ADB backup can extract app data, but requires USB debugging enabled. Use Cellebrite or Magnet AXIOM for physical extractions.',
    'macOS: Check ~/Library/Logs/DiagnosticReports for crash logs and ~/Library/Safari/History.db for browsing history.',
    'iOS: iTunes backup contains most user data. Use libimobiledevice for non-destructive extraction without triggering anti-forensic measures.',
    'Windows: Event Logs (Security, System, Application) are critical - use Event Viewer or EVTX parsers to analyze logon events (Event ID 4624/4625).',
    'Linux: The /proc filesystem contains running process info - capture /proc/[pid]/cmdline and /proc/[pid]/environ for live forensics.',
    'Network: Wireshark PCAP analysis can reveal C2 communications. Look for DNS tunneling and unusual port usage patterns.',
    'Windows: Prefetch files (C:\\Windows\\Prefetch) show program execution history - last run time and frequency.',
    'Android: SQLite databases in /data/data/ contain app messages, contacts, and call logs. Root access may be required.',
    'macOS: Unified Logs (log show --last 1h) contain system and app activity. Use Console.app or log command for extraction.',
    'Memory Forensics: Use Volatility to analyze RAM dumps - extract running processes, network connections, and encryption keys.',
    'Windows: Thumbcache files reveal files that were viewed but may have been deleted - useful for proving file access.',
    'Linux: Bash history (~/.bash_history) records commands. Check for anti-forensic commands like shred, dd, and secure-delete.',
    'iOS: Keychain stores passwords and certificates. Extract using tools like Keychain Dump or Elcomsoft Phone Breaker.',
    'Windows: $UsnJrnl (USN Change Journal) tracks all file system changes - essential for proving file creation/deletion timeline.',
    'Cloud Forensics: Check cloud sync logs (OneDrive, Google Drive) for file transfer evidence and timestamps.',
    'Mobile: Faraday bags prevent remote wiping during seizure - always use before powering off the device.',
    'Windows: Registry hives (SAM, SYSTEM, SOFTWARE) contain user accounts, installed software, and system configuration.',
    'Linux: File system timestamps (ctime, mtime, atime) can be altered - cross-reference with journal logs for verification.',
    'Android: FRP (Factory Reset Protection) can be bypassed legally with proper credentials - document the process.',
    'macOS: FileVault encryption keys are stored in the Keychain - extract before shutting down the Mac.',
    'Windows: Link files (.lnk) in Recent folder show recently opened files - even if the original file was deleted.',
    'Disk Forensics: Always create bit-for-bit forensic images using write blockers - never work on original evidence.',
    'Email Forensics: Check email headers for originating IP addresses and routing information to trace senders.',
    'Windows: Amcache.hve records application execution - useful when Prefetch is cleared.',
    'Linux: Check /etc/crontab and /var/spool/cron for scheduled tasks that may indicate persistence mechanisms.',
    'Mobile: SIM card data contains ICCID, IMSI, and stored SMS messages that survive factory resets.',
    'Windows: SRUM (System Resource Usage Monitor) tracks network usage and application resource consumption.',
    'macOS: FSEvents logs provide file system change history - useful for proving file access timeline.',
    'Anti-Forensics: Check for steganography tools (StegSolve, OpenStego) and encrypted containers (VeraCrypt).',
    'Windows: ShellBags registry entries show folders accessed via Windows Explorer - even network shares.',
    'Linux: /var/log/wtmp records login/logout history - use last command to view.',
    'Android: Google Takeout can provide cloud-synced data when physical access is limited.',
    'Windows: Timeline artifacts (ActivitiesCache.db) show user activity across apps and browsers.',
    'Memory: Malware often resides only in RAM - capture memory before shutting down the system.',
    'Windows: $LogFile (NTFS journal) records file system transactions - useful for proving file operations.',
    'iOS: Screen Time data reveals app usage patterns and restrictions set by the user.',
    'Linux: /var/log/kern.log contains kernel messages including USB device connections.',
    'Windows: Browser history databases (Chrome: History, Firefox: places.sqlite) contain visited URLs and timestamps.',
    'Android: App usage statistics (UsageStatsManager) record app foreground/background time.',
    'Windows: $I30 index entries in $MFT show directory contents - useful for proving file existence.',
    'macOS: KnowledgeC.db stores app usage and Siri suggestions - useful for timeline reconstruction.',
    'Windows: SRUM database tracks network connections per application - useful for proving internet access.',
    'Linux: Check /var/log/apache2/ or /var/log/nginx/ for web server access logs.',
    'Mobile: Baseband forensics can extract call logs and cell tower information from phone memory.',
    'Windows: Event Logs 4688 (process creation) with command-line auditing shows full process arguments.',
    'macOS: TCC.db (Transparency, Consent, and Control) shows which apps have camera/microphone access.',
    'Android: Check /data/system/users/0/accounts.db for Google and social media account information.'
  ];

  constructor(private reportService: ReportService, private caseService: CaseService, private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadDashboard();
    this.setDailyTip();
  }

  setDailyTip(): void {
    const today = new Date();
    const dayOfYear = Math.floor((today.getTime() - new Date(today.getFullYear(), 0, 0).getTime()) / 86400000);
    const index = dayOfYear % this.forensicTips.length;
    this.dailyTip = this.forensicTips[index];
  }

  loadDashboard(): void {
    this.reportService.getDashboard().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
    this.caseService.getAll().subscribe({
      next: (cases) => this.recentCases = cases.slice(0, 5)
    });
  }

  getStatusClass(status: string): string {
    return 'status-' + status.toLowerCase();
  }

  getPriorityClass(priority: string): string {
    return 'priority-' + priority.toLowerCase();
  }
}
