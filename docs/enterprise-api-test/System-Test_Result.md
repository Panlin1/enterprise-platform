# Enterprise-System 模块接口测试

```bash
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
# ===== 1. auth 登录拿 token =====
$c = Invoke-RestMethod http://localhost:8081/api/auth/captcha
$body = @{ username='admin'; password='123456'; captcha=$c.data.captchaCode; captchaId=$c.data.captchaId } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/auth/login -Body $body -ContentType 'application/json'
if ($login.code -ne 200) { "登录失败: $($login.message)"; return }
$h = @{ Authorization = "Bearer $($login.data.token)" }
"TOKEN=$($login.data.token)"
TOKEN=3037cbc8-4b7f-4fd4-a95d-cd16536bf9a6
```


```bash
# ===== 2. 菜单 =====
"=== 菜单树 ==="
=== 菜单树 ===
(Invoke-RestMethod -Uri "http://localhost:8083/api/system/menus/tree" -Headers $h) | ConvertTo-Json -Depth 5
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

"=== 菜单列表 ==="
=== 菜单列表 ===
(Invoke-RestMethod -Uri "http://localhost:8083/api/system/menus" -Headers $h) | ConvertTo-Json -Depth 3
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

                                  ]
                 },
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
```


```bash
# ===== 3. 字典 =====
"=== 字典类型 ==="
=== 字典类型 ===
(Invoke-RestMethod -Uri "http://localhost:8083/api/system/dicts" -Headers $h) | ConvertTo-Json -Depth 3
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
```
```bash
# ===== 4. 配置 =====
"=== 配置分页 ==="
(Invoke-RestMethod -Uri "http://localhost:8083/api/system/configs?page=1&size=10" -Headers $h) | ConvertTo-Json -Depth 4
{
    "code":  200,
    "message":  "success",
    "data":  {
                 "records":  [
                                 {
                                     "id":  1,
                                     "configKey":  "sys.account.captchaEnabled",
                                     "configValue":  "true",
                                     "configName":  "ç»å½æ¯å¦å¼å¯éªè¯ç ",
                                     "configType":  0
                                 },
                                 {
                                     "id":  2,
                                     "configKey":  "sys.account.loginFailMax",
                                     "configValue":  "5",
                                     "configName":  "ç»å½å¤±è´¥éå®éå¼",
                                     "configType":  0,
                                     "remark":  "è¶\u0085è¿åéå®ä¸æ®µæ¶é´"
                                 },
                                 {
                                     "id":  3,
                                     "configKey":  "sys.account.loginLockSeconds",
                                     "configValue":  "900",
                                     "configName":  "ç»å½éå®ç§æ°",
                                     "configType":  0,
                                     "remark":  "é»è®¤ 15 åé"
                                 },
                                 {
                                     "id":  4,
                                     "configKey":  "sys.user.initPassword",
                                     "configValue":  "123456",
                                     "configName":  "ç¨æ·åå§å¯ç ",
                                     "configType":  0,
                                     "remark":  "ä»\u0085åå»ºç¨æ·æ¶ä½¿ç¨ææçº¦å®ï¼å\u0085¥åºå¿\u0085é¡» BCrypt"
                                 }
                             ],
                 "total":  4,
                 "page":  1,
                 "size":  10
             },
    "success":  true
}
```


```bash
# ===== 5. 权限 =====
"=== 权限列表 ==="
=== 权限列表 ===

(Invoke-RestMethod -Uri "http://localhost:8083/api/system/permissions" -Headers $h) | ConvertTo-Json -Depth 3
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
```



{
"code": 401,
"message": "未认证或登录已失效",
"success": false
}
