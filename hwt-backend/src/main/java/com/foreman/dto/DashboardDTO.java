package com.foreman.dto;

public class DashboardDTO {
    private long totalCases;
    private long openCases;
    private long closedCases;
    private long totalTasks;
    private long queuedTasks;
    private long completedTasks;
    private long totalEvidence;
    private long activeUsers;

    public DashboardDTO() {}

    public long getTotalCases() { return totalCases; }
    public void setTotalCases(long totalCases) { this.totalCases = totalCases; }
    public long getOpenCases() { return openCases; }
    public void setOpenCases(long openCases) { this.openCases = openCases; }
    public long getClosedCases() { return closedCases; }
    public void setClosedCases(long closedCases) { this.closedCases = closedCases; }
    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }
    public long getQueuedTasks() { return queuedTasks; }
    public void setQueuedTasks(long queuedTasks) { this.queuedTasks = queuedTasks; }
    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }
    public long getTotalEvidence() { return totalEvidence; }
    public void setTotalEvidence(long totalEvidence) { this.totalEvidence = totalEvidence; }
    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
}
