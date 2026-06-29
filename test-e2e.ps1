$pass = 0; $fail = 0
$baseUrl = "http://localhost:8080"

function Test-Case($name, $block) {
    try {
        $result = Invoke-Command -ScriptBlock $block
        Write-Host "[OK]   $name"
        $script:pass++
        return $result
    } catch {
        $msg = $_.Exception.Message
        if ($msg.Length -gt 100) { $msg = $msg.Substring(0,100) }
        Write-Host "[FAIL] $name - $msg"
        $script:fail++
        return $null
    }
}

# === AUTH ===
Write-Host "`n========== 1. AUTH =========="
$loginBody = @{username="admin"; password="admin123"} | ConvertTo-Json
$login = Test-Case "Login" { Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json" }
$h = @{"Authorization"="Bearer $($login.token)"}
$uid = $login.userId
Write-Host "  Logged in as userId=$uid, roles=$($login.roles -join ',')"

# === USERS ===
Write-Host "`n========== 2. USERS =========="
$allUsers = Test-Case "Get all users" { Invoke-RestMethod -Uri "$baseUrl/api/users" -Headers $h }
Test-Case "Users count >= 1" { if ($allUsers.Count -lt 1) { throw "count=$($allUsers.Count)" } }
Test-Case "Get users by role ADMIN" { Invoke-RestMethod -Uri "$baseUrl/api/users?role=ADMIN" -Headers $h }
Test-Case "Get user by id" { Invoke-RestMethod -Uri "$baseUrl/api/users/1" -Headers $h }
Test-Case "Update user" { Invoke-RestMethod -Uri "$baseUrl/api/users/1" -Method PUT -Body (@{forename="Admin"; surname="User"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }

# === CASES ===
Write-Host "`n========== 3. CASES =========="
$case1 = Test-Case "Create case 1" { Invoke-RestMethod -Uri "$baseUrl/api/cases?userId=$uid" -Method POST -Body (@{caseName="Cyber Attack Investigation"; caseDescription="APT group targeting financial sector"; priority="CRITICAL"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
$case2 = Test-Case "Create case 2" { Invoke-RestMethod -Uri "$baseUrl/api/cases?userId=$uid" -Method POST -Body (@{caseName="Insider Threat"; caseDescription="Employee data exfiltration"; priority="HIGH"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
$case3 = Test-Case "Create case 3" { Invoke-RestMethod -Uri "$baseUrl/api/cases?userId=$uid" -Method POST -Body (@{caseName="Ransomware Incident"; caseDescription="Ransomware on production servers"; priority="CRITICAL"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Cases count = 3" { $r = Invoke-RestMethod -Uri "$baseUrl/api/cases" -Headers $h; if ($r.Count -ne 3) { throw "count=$($r.Count)" } }
Test-Case "Get case by id" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)" -Headers $h }
Test-Case "Update case" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)?userId=$uid" -Method PUT -Body (@{caseName="Cyber Attack Investigation - Phase 2"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Change case status" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/status" -Method PUT -Body (@{status="ACTIVE"; reason="Commenced"; userId="$uid"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Case history" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/history" -Headers $h }

# === TASKS ===
Write-Host "`n========== 4. TASKS =========="
$task1 = Test-Case "Create task 1" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/tasks?userId=$uid" -Method POST -Body (@{taskName="Network packet capture analysis"; background="Analyze 50GB pcap"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
$task2 = Test-Case "Create task 2" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/tasks?userId=$uid" -Method POST -Body (@{taskName="Malware reverse engineering"; background="Analyze malware sample"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
$task3 = Test-Case "Create task 3" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case2.id)/tasks?userId=$uid" -Method POST -Body (@{taskName="Access log review"; background="Review 3mo logs"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Tasks by case" { $r = Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/tasks" -Headers $h; if ($r.Count -lt 2) { throw "count=$($r.Count)" } }
Test-Case "Get task by id" { Invoke-RestMethod -Uri "$baseUrl/api/tasks/$($task1.id)" -Headers $h }
Test-Case "Update task" { Invoke-RestMethod -Uri "$baseUrl/api/tasks/$($task1.id)?userId=$uid" -Method PUT -Body (@{taskName="Network analysis - Phase 2"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Change task status" { Invoke-RestMethod -Uri "$baseUrl/api/tasks/$($task1.id)/status" -Method PUT -Body (@{status="ACTIVE"; note="Starting"; userId="$uid"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Add task note" { Invoke-RestMethod -Uri "$baseUrl/api/tasks/$($task1.id)/notes" -Method POST -Body (@{note="Found suspicious DNS queries to C2 domains"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Get task notes" { $r = Invoke-RestMethod -Uri "$baseUrl/api/tasks/$($task1.id)/notes" -Headers $h; if ($r.Count -lt 1) { throw "count=$($r.Count)" } }
Test-Case "Get task history" { Invoke-RestMethod -Uri "$baseUrl/api/tasks/$($task1.id)/history" -Headers $h }

# === EVIDENCE ===
Write-Host "`n========== 5. EVIDENCE =========="
$ev1 = Test-Case "Create evidence 1" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/evidence?userId=$uid" -Method POST -Body (@{reference="EVD-001"; comment="Seized HDD from suspect office"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
$ev2 = Test-Case "Create evidence 2" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/evidence?userId=$uid" -Method POST -Body (@{reference="EVD-002"; comment="Suspect mobile phone"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
$ev3 = Test-Case "Create evidence 3" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case2.id)/evidence?userId=$uid" -Method POST -Body (@{reference="EVD-003"; comment="USB drive found at desk"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "All evidence" { $r = Invoke-RestMethod -Uri "$baseUrl/api/evidence" -Headers $h; if ($r.Count -lt 3) { throw "count=$($r.Count)" } }
Test-Case "Get evidence by id" { Invoke-RestMethod -Uri "$baseUrl/api/evidence/$($ev1.id)" -Headers $h }
Test-Case "Update evidence" { Invoke-RestMethod -Uri "$baseUrl/api/evidence/$($ev1.id)?userId=$uid" -Method PUT -Body (@{reference="EVD-001-UPD"; comment="Updated: 2TB Seagate HDD"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Evidence status change" { Invoke-RestMethod -Uri "$baseUrl/api/evidence/$($ev1.id)/status" -Method PUT -Body (@{status="ACTIVE"; note="Logged into system"; userId="$uid"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Check-in evidence" { Invoke-RestMethod -Uri "$baseUrl/api/evidence/$($ev1.id)/check-in" -Method PUT -Body (@{custodian="John Smith"; comment="Received from field"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Check-out evidence" { Invoke-RestMethod -Uri "$baseUrl/api/evidence/$($ev1.id)/check-out" -Method PUT -Body (@{custodian="Jane Doe"; comment="Checked out for analysis"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Chain of custody" { $r = Invoke-RestMethod -Uri "$baseUrl/api/evidence/$($ev1.id)/chain-of-custody" -Headers $h; if ($r.Count -lt 2) { throw "count=$($r.Count)" } }
Test-Case "Evidence history" { Invoke-RestMethod -Uri "$baseUrl/api/evidence/$($ev1.id)/history" -Headers $h }
Test-Case "Case evidence" { $r = Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/evidence" -Headers $h; if ($r.Count -lt 2) { throw "count=$($r.Count)" } }

# === REPORTS ===
Write-Host "`n========== 6. REPORTS & DASHBOARD =========="
Test-Case "Dashboard report" { Invoke-RestMethod -Uri "$baseUrl/api/reports/dashboard" -Headers $h }
Test-Case "Dashboard alt" { Invoke-RestMethod -Uri "$baseUrl/api/dashboard" -Headers $h }
Test-Case "Case report" { Invoke-RestMethod -Uri "$baseUrl/api/reports/cases/$($case1.id)" -Headers $h }
Test-Case "Case activity report" { Invoke-RestMethod -Uri "$baseUrl/api/reports/cases/$($case1.id)/activity" -Headers $h }

# === CASE LINKING ===
Write-Host "`n========== 7. CASE LINKING =========="
Test-Case "Link cases" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/link" -Method POST -Body (@{linkedCaseId="$($case2.id)"; reason="Related incidents"; userId="$uid"} | ConvertTo-Json) -ContentType "application/json" -Headers $h }
Test-Case "Unlink cases" { Invoke-RestMethod -Uri "$baseUrl/api/cases/$($case1.id)/link/$($case2.id)?userId=$uid" -Method DELETE -Headers $h }

# === SUMMARY ===
Write-Host "`n========== SUMMARY =========="
$cases = Invoke-RestMethod -Uri "$baseUrl/api/cases" -Headers $h
$users = Invoke-RestMethod -Uri "$baseUrl/api/users" -Headers $h
$ev = Invoke-RestMethod -Uri "$baseUrl/api/evidence" -Headers $h
Write-Host "Users: $($users.Count), Cases: $($cases.Count), Evidence: $($ev.Count)"
Write-Host "`nTests: $pass passed, $fail failed, $($pass+$fail) total"
if ($fail -eq 0) { Write-Host "ALL TESTS PASSED!" -ForegroundColor Green } else { Write-Host "SOME TESTS FAILED!" -ForegroundColor Red }
