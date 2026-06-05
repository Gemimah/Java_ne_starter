$ErrorActionPreference = "Stop"
$base = "http://localhost:8080"
function J($o){ $o | ConvertTo-Json -Depth 8 }
$today = (Get-Date).ToString("yyyy-MM-dd")
$month = (Get-Date).Month
$year  = (Get-Date).Year

Write-Host "1) Admin login" -ForegroundColor Cyan
$admin = Invoke-RestMethod "$base/api/auth/login" -Method Post -ContentType application/json -Body (J @{ email="admin@wasac.com"; password="admin123" })
$ah = @{ Authorization = "Bearer $($admin.token)" }

Write-Host "2) Admin creates OPERATOR + FINANCE" -ForegroundColor Cyan
Invoke-RestMethod "$base/api/auth/register" -Method Post -Headers $ah -ContentType application/json -Body (J @{ fullNames="Op One"; email="operator@wasac.com"; phoneNumber="+250788111111"; password="oper123"; role="OPERATOR" }) | Out-Null
Invoke-RestMethod "$base/api/auth/register" -Method Post -Headers $ah -ContentType application/json -Body (J @{ fullNames="Fin One"; email="finance@wasac.com"; phoneNumber="+250788222222"; password="fin123"; role="FINANCE" }) | Out-Null

Write-Host "3) Public customer self-signup (forced CUSTOMER)" -ForegroundColor Cyan
Invoke-RestMethod "$base/api/auth/register" -Method Post -ContentType application/json -Body (J @{ fullNames="Jean Pierre"; email="jean@gmail.com"; phoneNumber="+250788333333"; password="cust123" }) | Out-Null
$cust = Invoke-RestMethod "$base/api/auth/login" -Method Post -ContentType application/json -Body (J @{ email="jean@gmail.com"; password="cust123" })
$ch = @{ Authorization = "Bearer $($cust.token)" }

Write-Host "   try public staff signup (should FAIL 403)" -ForegroundColor DarkGray
try { Invoke-RestMethod "$base/api/auth/register" -Method Post -ContentType application/json -Body (J @{ fullNames="Hacker"; email="hack@x.com"; phoneNumber="+250788999999"; password="hack123"; role="ADMIN" }); Write-Host "   UNEXPECTED success" -ForegroundColor Red } catch { Write-Host "   blocked as expected" -ForegroundColor Green }

Write-Host "4) Admin creates customer record" -ForegroundColor Cyan
$customer = Invoke-RestMethod "$base/api/customers" -Method Post -Headers $ah -ContentType application/json -Body (J @{ fullNames="Jean Pierre"; nationalId="1199880012345678"; email="jean@gmail.com"; phoneNumber="+250788333333"; address="Kigali"; status="ACTIVE" })
Write-Host "   customerId=$($customer.id)"

Write-Host "5) Admin creates meter" -ForegroundColor Cyan
$meter = Invoke-RestMethod "$base/api/meters" -Method Post -Headers $ah -ContentType application/json -Body (J @{ meterNumber="MTR-001"; meterType="WATER"; installationDate=$today; status="ACTIVE"; customerId=$customer.id })
Write-Host "   meterId=$($meter.id)"

Write-Host "6) Admin creates FLAT tariff" -ForegroundColor Cyan
$tariff = Invoke-RestMethod "$base/api/tariffs" -Method Post -Headers $ah -ContentType application/json -Body (J @{ meterType="WATER"; version=1; effectiveMonth=$month; effectiveYear=$year; tariffType="FLAT"; flatRate=500; fixedServiceCharge=1000; vatRate=18; latePenaltyRate=10 })
Write-Host "   tariffId=$($tariff.id)"
Invoke-RestMethod "$base/api/tariffs/active/WATER" -Headers $ah | Out-Null

Write-Host "7) Operator captures reading" -ForegroundColor Cyan
$op = Invoke-RestMethod "$base/api/auth/login" -Method Post -ContentType application/json -Body (J @{ email="operator@wasac.com"; password="oper123" })
$oh = @{ Authorization = "Bearer $($op.token)" }
$reading = Invoke-RestMethod "$base/api/readings" -Method Post -Headers $oh -ContentType application/json -Body (J @{ meterId=$meter.id; previousReading=100; currentReading=150; readingDate=$today })
Write-Host "   readingId=$($reading.id) consumption=$($reading.consumption)"

Write-Host "8) Generate bill from reading" -ForegroundColor Cyan
$bill = Invoke-RestMethod "$base/api/bills/generate/$($reading.id)" -Method Post -Headers $ah
Write-Host "   billId=$($bill.id) total=$($bill.totalAmount) status=$($bill.status) due=$($bill.dueDate)"

Write-Host "9) Finance approves bill" -ForegroundColor Cyan
$fin = Invoke-RestMethod "$base/api/auth/login" -Method Post -ContentType application/json -Body (J @{ email="finance@wasac.com"; password="fin123" })
$fh = @{ Authorization = "Bearer $($fin.token)" }
$approved = Invoke-RestMethod "$base/api/bills/$($bill.id)/approve" -Method Put -Headers $fh
Write-Host "   status=$($approved.status)"

Write-Host "10) Customer views own bills" -ForegroundColor Cyan
$mybills = Invoke-RestMethod "$base/api/bills/customer/$($customer.id)" -Headers $ch
Write-Host "   bills=$($mybills.Count)"

Write-Host "11) Full payment" -ForegroundColor Cyan
$pay = Invoke-RestMethod "$base/api/payments" -Method Post -Headers $fh -ContentType application/json -Body (J @{ billId=$bill.id; amountPaid=$bill.totalAmount; paymentMethod="MOBILE_MONEY"; paymentDate=$today })
Write-Host "   paymentId=$($pay.id)"
$billAfter = Invoke-RestMethod "$base/api/bills/$($bill.id)" -Headers $ah
Write-Host "   bill status now=$($billAfter.status) outstanding=$($billAfter.outstandingBalance)"

Write-Host "12) Notifications (app) for customer" -ForegroundColor Cyan
$notifs = Invoke-RestMethod "$base/api/notifications/customer/$($customer.id)" -Headers $ch
Write-Host "   notifications=$($notifs.Count)"
$notifs | ForEach-Object { Write-Host "    - $($_.subject)" }

Write-Host "13) Payment history" -ForegroundColor Cyan
$hist = Invoke-RestMethod "$base/api/payments/customer/$($customer.id)" -Headers $ch
Write-Host "   payments=$($hist.Count)"

Write-Host "ALL STEPS DONE" -ForegroundColor Green
