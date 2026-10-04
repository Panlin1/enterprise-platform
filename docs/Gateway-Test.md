PS C:\Users\Lenovo> [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
PS C:\Users\Lenovo> $BASE = "http://localhost:8084"
PS C:\Users\Lenovo> # 1. 结构对比
PS C:\Users\Lenovo> $direct = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/captcha"
PS C:\Users\Lenovo> $cap    = Invoke-RestMethod -Uri "$BASE/api/auth/captcha"
PS C:\Users\Lenovo> "直连: code=$($direct.code) | 网关: code=$($cap.code)"
直连: code=200 | 网关: code=200
PS C:\Users\Lenovo> # 2. 经网关登录
PS C:\Users\Lenovo> $body = @{ username='admin'; password='123456'; captcha=$cap.data.captchaCode; captchaId=$cap.data.captchaId } | ConvertTo-Json
PS C:\Users\Lenovo> $login = Invoke-RestMethod -Method Post -Uri "$BASE/api/auth/login" -Body $body -ContentType 'application/json'
PS C:\Users\Lenovo> if ($login.code -ne 200) { "登录失败: $($login.message)"; return }
PS C:\Users\Lenovo> $h = @{ Authorization = "Bearer $($login.data.token)" }
PS C:\Users\Lenovo> "登录: code=$($login.code) TOKEN OK"
登录: code=200 TOKEN OK
PS C:\Users\Lenovo> # 3. me 对比（值应完全一致）
PS C:\Users\Lenovo> $me1 = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/me" -Headers $h
PS C:\Users\Lenovo> $me2 = Invoke-RestMethod -Uri "$BASE/api/auth/me" -Headers $h
PS C:\Users\Lenovo> "me 一致: 直连=$($me1.data.username) 网关=$($me2.data.username) => $($me1.data.username -eq $me2.data.username)"
me 一致: 直连=admin 网关=admin => True
PS C:\Users\Lenovo> # 4. user 模块
PS C:\Users\Lenovo> "=== user 分页 ==="
=== user 分页 ===
PS C:\Users\Lenovo> (Invoke-RestMethod -Uri "$BASE/api/users?page=1&size=10" -Headers $h) | ConvertTo-Json -Depth 3
{
"code":  200,
"message":  "success",
"data":  {
"records":  [
{
"id":  3,
"username":  "test01",
"nickname":  "????",
"gender":  0,
"status":  1,
"roleIds":  "2",
"roleCodes":  "USER",
"createdAt":  "2026-09-24 15:56:46",
"updatedAt":  "2026-09-24 15:56:46"
},
{
"id":  1,
"username":  "admin",
"nickname":  "ç®¡çå",
"realName":  "ç³»ç»ç®¡çå",
"email":  "admin@example.com",
"phone":  "13800000001",
"gender":  0,
"status":  1,
"remark":  "é»è®¤è¶\u0085çº§ç®¡çå",
"roleIds":  "1",
"roleCodes":  "ADMIN",
"createdAt":  "2026-09-22 16:05:12",
"updatedAt":  "2026-09-22 16:05:12"
}
],
"total":  2,
"page":  1,
"size":  10
},
"success":  true
}
PS C:\Users\Lenovo> "=== user 详情 ==="
=== user 详情 ===
PS C:\Users\Lenovo> (Invoke-RestMethod -Uri "$BASE/api/users/1" -Headers $h) | ConvertTo-Json -Depth 3
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
PS C:\Users\Lenovo> # 5. system 模块
PS C:\Users\Lenovo> "=== 菜单树 ==="
=== 菜单树 ===
PS C:\Users\Lenovo> (Invoke-RestMethod -Uri "$BASE/api/system/menus/tree" -Headers $h) | ConvertTo-Json -Depth 5
{
"code":  200,
"message":  "success",
"data":  [
{
"id":  1,
"parentId":  0,
"menuName":  "ç³»ç»ç®¡ç",
"menuType":  1,
"path":  "/system",
"component":  "Layout",
"permissionCode":  "system",
"icon":  "Setting",
"sort":  1,
"visible":  1,
"status":  1,
"children":  [
{
"id":  2,
"parentId":  1,
"menuName":  "ç¨æ·ç®¡ç",
"menuType":  2,
"path":  "user",
"component":  "system/user/index",
"permissionCode":  "system:user:list",
"icon":  "User",
"sort":  1,
"visible":  1,
"status":  1,
"children":  [

                                                       ]
                                      },
                                      {
                                          "id":  3,
                                          "parentId":  1,
                                          "menuName":  "è§è²ç®¡ç",
                                          "menuType":  2,
                                          "path":  "role",
                                          "component":  "system/role/index",
                                          "permissionCode":  "system:role:list",
                                          "icon":  "UserFilled",
                                          "sort":  2,
                                          "visible":  1,
                                          "status":  1,
                                          "children":  [

                                                       ]
                                      },
                                      {
                                          "id":  4,
                                          "parentId":  1,
                                          "menuName":  "æéç®¡ç",
                                          "menuType":  2,
                                          "path":  "permission",
                                          "component":  "system/permission/index",
                                          "permissionCode":  "system:permission:list",
                                          "icon":  "Key",
                                          "sort":  3,
                                          "visible":  1,
                                          "status":  1,
                                          "children":  [

                                                       ]
                                      },
                                      {
                                          "id":  5,
                                          "parentId":  1,
                                          "menuName":  "èåç®¡ç",
                                          "menuType":  2,
                                          "path":  "menu",
                                          "component":  "system/menu/index",
                                          "permissionCode":  "system:menu:list",
                                          "icon":  "Menu",
                                          "sort":  4,
                                          "visible":  1,
                                          "status":  1,
                                          "children":  [

                                                       ]
                                      },
                                      {
                                          "id":  6,
                                          "parentId":  1,
                                          "menuName":  "å­å\u0085¸ç®¡ç",
                                          "menuType":  2,
                                          "path":  "dict",
                                          "component":  "system/dict/index",
                                          "permissionCode":  "system:dict:list",
                                          "icon":  "Collection",
                                          "sort":  5,
                                          "visible":  1,
                                          "status":  1,
                                          "children":  [

                                                       ]
                                      },
                                      {
                                          "id":  7,
                                          "parentId":  1,
                                          "menuName":  "åæ°é\u0085ç½®",
                                          "menuType":  2,
                                          "path":  "config",
                                          "component":  "system/config/index",
                                          "permissionCode":  "system:config:list",
                                          "icon":  "Tools",
                                          "sort":  6,
                                          "visible":  1,
                                          "status":  1,
                                          "children":  [

                                                       ]
                                      }
                                  ]
                 },
                 {
                     "id":  8,
                     "parentId":  0,
                     "menuName":  "ä¸ªäººä¸­å¿",
                     "menuType":  2,
                     "path":  "/profile",
                     "component":  "profile/index",
                     "icon":  "User",
                     "sort":  9,
                     "visible":  1,
                     "status":  1,
                     "children":  [

                                  ]
                 }
             ],
    "success":  true
}
PS C:\Users\Lenovo> "=== 字典 ==="
=== 字典 ===
PS C:\Users\Lenovo> (Invoke-RestMethod -Uri "$BASE/api/system/dicts" -Headers $h) | ConvertTo-Json -Depth 3
{
"code":  200,
"message":  "success",
"data":  [
{
"createdAt":  "2026-09-24 15:30:13",
"updatedAt":  "2026-09-24 15:30:13",
"createdBy":  1,
"updatedBy":  1,
"id":  1,
"dictType":  "user_status",
"dictName":  "ç¨æ·ç¶æ",
"status":  1,
"remark":  "å¯ç¨/ç¦ç¨"
},
{
"createdAt":  "2026-09-24 15:30:13",
"updatedAt":  "2026-09-24 15:30:13",
"createdBy":  1,
"updatedBy":  1,
"id":  2,
"dictType":  "gender",
"dictName":  "æ§å«",
"status":  1
},
{
"createdAt":  "2026-09-24 15:30:13",
"updatedAt":  "2026-09-24 15:30:13",
"createdBy":  1,
"updatedBy":  1,
"id":  3,
"dictType":  "yes_no",
"dictName":  "æ¯å¦",
"status":  1
}
],
"success":  true
}
PS C:\Users\Lenovo> "=== 权限列表 ==="
=== 权限列表 ===
PS C:\Users\Lenovo> (Invoke-RestMethod -Uri "$BASE/api/system/permissions" -Headers $h) | ConvertTo-Json -Depth 3
{
"code":  200,
"message":  "success",
"data":  [
{
"id":  100,
"permissionCode":  "system",
"permissionName":  "ç³»ç»ç®¡ç",
"permissionType":  1,
"parentId":  0,
"sort":  1,
"status":  1
},
{
"id":  110,
"permissionCode":  "system:user",
"permissionName":  "ç¨æ·ç®¡ç",
"permissionType":  1,
"parentId":  100,
"sort":  1,
"status":  1
},
{
"id":  111,
"permissionCode":  "system:user:list",
"permissionName":  "ç¨æ·æ¥è¯¢",
"permissionType":  2,
"parentId":  110,
"sort":  1,
"status":  1
},
{
"id":  121,
"permissionCode":  "system:role:list",
"permissionName":  "è§è²æ¥è¯¢",
"permissionType":  2,
"parentId":  120,
"sort":  1,
"status":  1
},
{
"id":  131,
"permissionCode":  "system:permission:list",
"permissionName":  "æéæ¥è¯¢",
"permissionType":  2,
"parentId":  130,
"sort":  1,
"status":  1
},
{
"id":  141,
"permissionCode":  "system:menu:list",
"permissionName":  "èåæ¥è¯¢",
"permissionType":  2,
"parentId":  140,
"sort":  1,
"status":  1
},
{
"id":  151,
"permissionCode":  "system:dict:list",
"permissionName":  "å­å\u0085¸æ¥è¯¢",
"permissionType":  2,
"parentId":  150,
"sort":  1,
"status":  1
},
{
"id":  161,
"permissionCode":  "system:config:list",
"permissionName":  "åæ°æ¥è¯¢",
"permissionType":  2,
"parentId":  160,
"sort":  1,
"status":  1
},
{
"id":  112,
"permissionCode":  "system:user:add",
"permissionName":  "ç¨æ·æ°å¢",
"permissionType":  2,
"parentId":  110,
"sort":  2,
"status":  1
},
{
"id":  120,
"permissionCode":  "system:role",
"permissionName":  "è§è²ç®¡ç",
"permissionType":  1,
"parentId":  100,
"sort":  2,
"status":  1
},
{
"id":  122,
"permissionCode":  "system:role:add",
"permissionName":  "è§è²æ°å¢",
"permissionType":  2,
"parentId":  120,
"sort":  2,
"status":  1
},
{
"id":  142,
"permissionCode":  "system:menu:add",
"permissionName":  "èåæ°å¢",
"permissionType":  2,
"parentId":  140,
"sort":  2,
"status":  1
},
{
"id":  113,
"permissionCode":  "system:user:update",
"permissionName":  "ç¨æ·ä¿®æ¹",
"permissionType":  2,
"parentId":  110,
"sort":  3,
"status":  1
},
{
"id":  123,
"permissionCode":  "system:role:update",
"permissionName":  "è§è²ä¿®æ¹",
"permissionType":  2,
"parentId":  120,
"sort":  3,
"status":  1
},
{
"id":  130,
"permissionCode":  "system:permission",
"permissionName":  "æéç®¡ç",
"permissionType":  1,
"parentId":  100,
"sort":  3,
"status":  1
},
{
"id":  143,
"permissionCode":  "system:menu:update",
"permissionName":  "èåä¿®æ¹",
"permissionType":  2,
"parentId":  140,
"sort":  3,
"status":  1
},
{
"id":  114,
"permissionCode":  "system:user:delete",
"permissionName":  "ç¨æ·å é¤",
"permissionType":  2,
"parentId":  110,
"sort":  4,
"status":  1
},
{
"id":  124,
"permissionCode":  "system:role:delete",
"permissionName":  "è§è²å é¤",
"permissionType":  2,
"parentId":  120,
"sort":  4,
"status":  1
},
{
"id":  140,
"permissionCode":  "system:menu",
"permissionName":  "èåç®¡ç",
"permissionType":  1,
"parentId":  100,
"sort":  4,
"status":  1
},
{
"id":  144,
"permissionCode":  "system:menu:delete",
"permissionName":  "èåå é¤",
"permissionType":  2,
"parentId":  140,
"sort":  4,
"status":  1
},
{
"id":  115,
"permissionCode":  "system:user:export",
"permissionName":  "ç¨æ·å¯¼åº",
"permissionType":  2,
"parentId":  110,
"sort":  5,
"status":  1
},
{
"id":  150,
"permissionCode":  "system:dict",
"permissionName":  "å­å\u0085¸ç®¡ç",
"permissionType":  1,
"parentId":  100,
"sort":  5,
"status":  1
},
{
"id":  116,
"permissionCode":  "system:user:status",
"permissionName":  "ç¨æ·ç¶æ",
"permissionType":  2,
"parentId":  110,
"sort":  6,
"status":  1
},
{
"id":  160,
"permissionCode":  "system:config",
"permissionName":  "åæ°é\u0085ç½®",
"permissionType":  1,
"parentId":  100,
"sort":  6,
"status":  1
},
{
"id":  117,
"permissionCode":  "system:user:password",
"permissionName":  "éç½®å¯ç ",
"permissionType":  2,
"parentId":  110,
"sort":  7,
"status":  1
}
],
"success":  true
}
PS C:\Users\Lenovo> # 6. 登出 → 401 验证
PS C:\Users\Lenovo> "=== 登出 ==="
=== 登出 ===
PS C:\Users\Lenovo> (Invoke-RestMethod -Method Post -Uri "$BASE/api/auth/logout" -Headers $h) | ConvertTo-Json -Depth 3
{
"code":  200,
"message":  "success",
"success":  true
}
PS C:\Users\Lenovo> "=== 登出后再访问（预期 401）==="
=== 登出后再访问（预期 401）===
PS C:\Users\Lenovo> try { Invoke-RestMethod -Uri "$BASE/api/users/1" -Headers $h } catch { $_.ErrorDetails.Message }

code msg       data
---- ---       ----
401 未登录或登录已过期
