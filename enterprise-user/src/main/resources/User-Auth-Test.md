```bash
# ===== 1. 登录拿 token =====
$c = Invoke-RestMethod http://localhost:8081/api/auth/captcha
$body = @{ username='admin'; password='123456'; captcha=$c.data.captchaCode; captchaId=$c.data.captchaId } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/auth/login -Body $body -ContentType 'application/json'
if ($login.code -ne 200) { "登录失败: $($login.message)"; return }
$h = @{ Authorization = "Bearer $($login.data.token)" }
"TOKEN=$($login.data.token)"

# ===== 2. 分页 =====
(Invoke-RestMethod -Uri "http://localhost:8082/api/users?page=1&size=10" -Headers $h) | ConvertTo-Json -Depth 5

# ===== 3. 详情（admin）=====
(Invoke-RestMethod -Uri "http://localhost:8082/api/users/1" -Headers $h) | ConvertTo-Json -Depth 5

# ===== 4. 新增 =====
$nc = @{ username='test01'; password='123456'; nickname='测试用户'; roleIds=@(2) } | ConvertTo-Json
(Invoke-RestMethod -Method Post -Uri "http://localhost:8082/api/users" -Headers $h -Body $nc -ContentType 'application/json') | ConvertTo-Json -Depth 5

# ===== 5. 修改 =====
$uc = @{ nickname='改名' } | ConvertTo-Json
(Invoke-RestMethod -Method Put -Uri "http://localhost:8082/api/users/2" -Headers $h -Body $uc -ContentType 'application/json') | ConvertTo-Json -Depth 5

# ===== 6. 改状态 =====
$sc = @{ status=0 } | ConvertTo-Json
(Invoke-RestMethod -Method Put -Uri "http://localhost:8082/api/users/2/status" -Headers $h -Body $sc -ContentType 'application/json') | ConvertTo-Json -Depth 5

# ===== 7. 改密码 =====
$pc = @{ newPassword='654321' } | ConvertTo-Json
(Invoke-RestMethod -Method Put -Uri "http://localhost:8082/api/users/2/password" -Headers $h -Body $pc -ContentType 'application/json') | ConvertTo-Json -Depth 5

# ===== 8. 删除 =====
(Invoke-RestMethod -Method Delete -Uri "http://localhost:8082/api/users/2" -Headers $h) | ConvertTo-Json -Depth 5
```

Result；
PS C:\Users\Lenovo> # ===== 3. 详情（admin）=====
PS C:\Users\Lenovo> (Invoke-RestMethod -Uri "http://localhost:8082/api/users/1" -Headers $h) | ConvertTo-Json -Depth 5
{
"code":  200,
"message":  "success",
"data":  {
"id":  1,
"username":  "admin",
"nickname":  "ç®¡çå",
"realName":  "ç³»ç»ç®¡çå",
"email":  "admin@example.com",
"phone":  "13800000001",
"gender":  0,
"status":  1,
"remark":  "é»è®¤è¶\u0085çº§ç®¡çå",
"roleIds":  [
1
],
"roleCodes":  [
"ADMIN"
],
"createdAt":  "2026-09-22 16:05:12",
"updatedAt":  "2026-09-22 16:05:12"
},
"success":  true
}
PS C:\Users\Lenovo>
PS C:\Users\Lenovo> # ===== 4. 新增 =====
PS C:\Users\Lenovo> $nc = @{ username='test01'; password='123456'; nickname='测试用户'; roleIds=@(2) } | ConvertTo-Json
PS C:\Users\Lenovo> (Invoke-RestMethod -Method Post -Uri "http://localhost:8082/api/users" -Headers $h -Body $nc -ContentType 'application/json') | ConvertTo-Json -Depth 5
{
"code":  200,
"message":  "success",
"data":  3,
"success":  true
}
PS C:\Users\Lenovo> # ===== 5. 修改 =====
PS C:\Users\Lenovo> $uc = @{ nickname='改名' } | ConvertTo-Json
PS C:\Users\Lenovo> (Invoke-RestMethod -Method Put -Uri "http://localhost:8082/api/users/2" -Headers $h -Body $uc -ContentType 'application/json') | ConvertTo-Json -Depth 5
{
"code":  200,
"message":  "success",
"success":  true
}
PS C:\Users\Lenovo> # ===== 6. 改状态 =====
PS C:\Users\Lenovo> $sc = @{ status=0 } | ConvertTo-Json
PS C:\Users\Lenovo> (Invoke-RestMethod -Method Put -Uri "http://localhost:8082/api/users/2/status" -Headers $h -Body $sc -ContentType 'application/json') | ConvertTo-Json -Depth 5
{
"code":  200,
"message":  "success",
"success":  true
}
PS C:\Users\Lenovo> # ===== 7. 改密码 =====
PS C:\Users\Lenovo> $pc = @{ newPassword='654321' } | ConvertTo-Json
PS C:\Users\Lenovo> (Invoke-RestMethod -Method Put -Uri "http://localhost:8082/api/users/2/password" -Headers $h -Body $pc -ContentType 'application/json') | ConvertTo-Json -Depth 5
{
"code":  200,
"message":  "success",
"success":  true
}
PS C:\Users\Lenovo> # ===== 8. 删除 =====
PS C:\Users\Lenovo> (Invoke-RestMethod -Method Delete -Uri "http://localhost:8082/api/users/2" -Headers $h) | ConvertTo-Json -Depth 5
{
"code":  200,
"message":  "success",
"success":  true
}
