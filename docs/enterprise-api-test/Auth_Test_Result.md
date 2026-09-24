# enterprise-auth 模块测试

## /api/auth/captcha   get

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "captchaId": "acd6805ee1064d3781443b3d6c27e3ec",
    "imageBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAAoCAIAAAC6iKlyAAACcklEQVR4Xu3ZMU7kMBTGcS4AW9GuqBZugKDYAho0266oOAyH4RCcgQtQrvYGr6HHmk88Wf7sZyfj2Mlg6V/M+CVC+skyCZyIfIwadMJLoyUa0I0a0I0a0I0a0I0a0I0a0I3qA/1+8YsXj7s+0LK3Nrhf//3hxU3XDRqluAd0pIcf93Z8S5Bvvbu9RM5aP/Mt2unlGeLRpOnN32vEl/nT1AVGz2+PrhbQhdYulQ2gDW7FimqWjLLQs5VBjM9rgQalz83QWevUuj3F1xTlPGWfGNWENkbRqRZo6kmCkzprnaW0p/ga1ZyhzMRoWejsFDEitrb/KzGlLEQWrPsfeGpAT1KGb5QY9Yfm3Xp1/dMle+7Cxw+29lfsKQpMy5VtX21ZaB1Fp4i3MwK3g44+/wUx3CHQhcqFxKgmtBHfpaWgkYOGuM3NcLOhOf5xk4jRqqGxjscPm1vhSmSDixH7snX2IDZqBG1YF0IjcOMED4pSFk6FoIPF2b5aTWge+dPUBZOgcZnb18xtU9pTSZzLSuwvzmtx6OwFAWK06Ot4wG1T2lOhpw75Oih4fV79ocXc1CgKjfxTWwXZ0Zje/f+tATQ4KKpYLw6t09QFknv32+X+wIS3G0lTInsKSh89GB1oXRPajm/0C6w5teZ7kbO2Ke2pTWlPS1oLtOSsS14RsbWjjiilLDnKwzd1C2i+xYiJd1+7uMTaIepJws2GLrnArgJ0s0qgtZR1r44WWtL/J+vSlqBlurWshvv4oVF3641BH1Lfrf2NoFEv7grQL+dPvLjy2ltXgJbNWrfkrgMt27SWhtzfHRo1sK4GLRu3Xrqa0DKs030CrucyrG3yg4YAAAAASUVORK5CYII=",
    "captchaCode": "BGWK"
  },
  "success": true
}
```


## /api/auth/login  post


```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "5bc1108e-9d9d-4442-8584-44da57fdcc86",
    "tokenType": "Bearer",
    "expiresIn": 7200
  },
  "success": true
}
```


## /api/auth/logout  post

需要添加请求头进行验证校验，具体内容如下所示:

```text
Headers:
    "Authorization" : "Bearer 5bc1108e-9d9d-4442-8584-44da57fdcc86"
```
参数说明：
> "token": "5bc1108e-9d9d-4442-8584-44da57fdcc86",
> "tokenType": "Bearer",

```json
{
  "code": 200,
  "message": "success",
  "success": true
}
```


## /api/auth/me  get

需要添加请求头进行验证校验，具体内容如下所示:

```text
Headers:
    "Authorization" : "Bearer 5bc1108e-9d9d-4442-8584-44da57fdcc86"
```
参数说明：
> "token": "5bc1108e-9d9d-4442-8584-44da57fdcc86",
> "tokenType": "Bearer",

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "realName": "系统管理员",
    "email": "admin@example.com",
    "phone": "13800000001",
    "roles": [
      "ADMIN"
    ],
    "permissions": []
  },
  "success": true
}
```




