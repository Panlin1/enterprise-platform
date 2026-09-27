
```bash
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$GW = "http://localhost:8084"

# ===== 1-2 网关健康与路由 =====
"=== 1. 网关健康 ==="
(Invoke-RestMethod -Uri "$GW/actuator/health") | ConvertTo-Json -Depth 3
"=== 2. 路由表 ==="
(Invoke-RestMethod -Uri "$GW/actuator/gateway/routes").routeDefinitions | Select-Object id, uri | Format-Table -AutoSize

# ===== 3. 匿名访问 → 预期 401 =====
"=== 3. 匿名访问 user（预期 401）==="
try { Invoke-RestMethod -Uri "$GW/api/users/1" } catch { $_.ErrorDetails.Message }

# ===== 4-5. 登录链路 =====
"=== 4. 验证码（白名单放行）==="
$c = Invoke-RestMethod -Uri "$GW/api/auth/captcha"
$c | ConvertTo-Json -Depth 3
"=== 5. 登录 ==="
$body = @{ username='admin'; password='123456'; captcha=$c.data.captchaCode; captchaId=$c.data.captchaId } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "$GW/api/auth/login" -Body $body -ContentType 'application/json'
if ($login.code -ne 200) { "登录失败: $($login.message)"; return }
$h = @{ Authorization = "Bearer $($login.data.token)" }
"登录成功 TOKEN=$($login.data.token)"

# ===== 6. 我的信息 =====
"=== 6. /api/auth/me ==="
(Invoke-RestMethod -Uri "$GW/api/auth/me" -Headers $h) | ConvertTo-Json -Depth 3

# ===== 7-8. user 模块（经网关 lb://enterprise-user）=====
"=== 7. user 分页 ==="
(Invoke-RestMethod -Uri "$GW/api/users?page=1&size=10" -Headers $h) | ConvertTo-Json -Depth 4
"=== 8. user 详情 admin ==="
(Invoke-RestMethod -Uri "$GW/api/users/1" -Headers $h) | ConvertTo-Json -Depth 3

# ===== 9-12. system 模块（经网关 lb://enterprise-system）=====
"=== 9. 菜单树 ==="
(Invoke-RestMethod -Uri "$GW/api/system/menus/tree" -Headers $h) | ConvertTo-Json -Depth 5
"=== 10. 字典 ==="
(Invoke-RestMethod -Uri "$GW/api/system/dicts" -Headers $h) | ConvertTo-Json -Depth 3
"=== 11. 配置分页 ==="
(Invoke-RestMethod -Uri "$GW/api/system/configs?page=1&size=10" -Headers $h) | ConvertTo-Json -Depth 4
"=== 12. 权限列表 ==="
(Invoke-RestMethod -Uri "$GW/api/system/permissions" -Headers $h) | ConvertTo-Json -Depth 3

# ===== 13-14. 登出与失效验证 =====
"=== 13. 登出 ==="
(Invoke-RestMethod -Method Post -Uri "$GW/api/auth/logout" -Headers $h) | ConvertTo-Json -Depth 3
"=== 14. 登出后访问（预期 401）==="
try { Invoke-RestMethod -Uri "$GW/api/users/1" -Headers $h } catch { $_.ErrorDetails.Message }

# ===== 15.（可选）user 写链路：新增 → 修改 → 删除 =====
"=== 15. user 写链路 ==="
$nc = @{ username='gwtest01'; password='123456'; nickname='网关测试'; roleIds=@(2) } | ConvertTo-Json
$r = Invoke-RestMethod -Method Post -Uri "$GW/api/users" -Headers $h -Body $nc -ContentType 'application/json'
"新增: code=$($r.code) id=$($r.data)"
$uid = $r.data
$uc = @{ nickname='网关改名' } | ConvertTo-Json
$ru = Invoke-RestMethod -Method Put -Uri "$GW/api/users/$uid" -Headers $h -Body $uc -ContentType 'application/json'
"修改: code=$($ru.code)"
$rd = Invoke-RestMethod -Method Delete -Uri "$GW/api/users/$uid" -Headers $h
"删除: code=$($rd.code)"

```
