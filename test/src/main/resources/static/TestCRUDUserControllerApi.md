
# 用户管理
## 获取所有用户信息
**URL:** `http://127.0.0.1:4612/user/`

**Type:** `GET`

**Author:** Dominick Li

**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 获取所有用户信息

**Request-headers:**

Header | Type|Description|Required|Since
---|---|---|---|----
token|string|身份凭证|false|-





**Request-example:**
```
curl -X GET -H 'token' -i http://127.0.0.1:4612/user/
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|No comments found.|-
msg|string|No comments found.|-
data|array|No comments found.|-
└─id|int32|No comments found.|-
└─createTime|string|No comments found.|-
└─lastmodifiedTime|string|No comments found.|-
└─username|string|用户名称|-
└─password|string|密码|-
└─age|int32|年龄|-
timestamp|string|No comments found.|-

**Response-example:**
```
{
  "code": 782,
  "msg": "xg3d6t",
  "data": [
    {
      "id": 598,
      "createTime": "2022-10-25 11:01:31",
      "lastmodifiedTime": "2022-10-25 11:01:31",
      "username": "kendall.hoppe",
      "password": "5ito4e",
      "age": 4
    }
  ],
  "timestamp": "2022-10-25 11:01:31"
}
```

## 根据用户id获取用户信息
**URL:** `http://127.0.0.1:4612/user/{id}`

**Type:** `GET`

**Author:** Dominick Li

**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 根据用户id获取用户信息

**Request-headers:**

Header | Type|Description|Required|Since
---|---|---|---|----
token|string|身份凭证|false|-


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
id|string|用户Id|true|-



**Request-example:**
```
curl -X GET -H 'token' -i http://127.0.0.1:4612/user/83
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|No comments found.|-
msg|string|No comments found.|-
data|object|No comments found.|-
└─id|int32|No comments found.|-
└─createTime|string|No comments found.|-
└─lastmodifiedTime|string|No comments found.|-
└─username|string|用户名称|-
└─password|string|密码|-
└─age|int32|年龄|-
timestamp|string|No comments found.|-

**Response-example:**
```
{
  "code": 615,
  "msg": "6o8ykr",
  "data": {
    "id": 803,
    "createTime": "2022-10-25 11:01:31",
    "lastmodifiedTime": "2022-10-25 11:01:31",
    "username": "kendall.hoppe",
    "password": "mll39f",
    "age": 4
  },
  "timestamp": "2022-10-25 11:01:31"
}
```

## 登录 入参方式: @RequestParam
**URL:** `http://127.0.0.1:4612/user/login`

**Type:** `POST`

**Author:** Dominick Li

**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 登录 入参方式: @RequestParam

**Request-headers:**

Header | Type|Description|Required|Since
---|---|---|---|----
token|string|身份凭证|false|-



**Query-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
username|string|用户名|true|-
password|string|密码|true|-


**Request-example:**
```
curl -X POST -H 'token' -i http://127.0.0.1:4612/user/login --data 'password=2j8q1c&username=kendall.hoppe'
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|No comments found.|-
msg|string|No comments found.|-
data|object|No comments found.|-
timestamp|string|No comments found.|-

**Response-example:**
```
{
  "code": 702,
  "msg": "n4611m",
  "data": {
    "waring": "You may have used non-display generics."
  },
  "timestamp": "2022-10-25 11:01:31"
}
```

## 添加用户 入参方式: @RequestBody
**URL:** `http://127.0.0.1:4612/user/`

**Type:** `POST`

**Author:** Dominick Li

**Content-Type:** `application/json; charset=utf-8`

**Description:** 添加用户 入参方式: @RequestBody

**Request-headers:**

Header | Type|Description|Required|Since
---|---|---|---|----
token|string|身份凭证|false|-




**Body-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
id|int32|No comments found.|false|-
createTime|string|No comments found.|false|-
lastmodifiedTime|string|No comments found.|false|-
username|string|用户名称|true|-
password|string|密码|true|-
age|int32|年龄|false|-

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -H 'token' -i http://127.0.0.1:4612/user/ --data '{
  "id": 694,
  "createTime": "2022-10-25 11:01:31",
  "lastmodifiedTime": "2022-10-25 11:01:31",
  "username": "kendall.hoppe",
  "password": "4af8rl",
  "age": 4
}'
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|No comments found.|-
msg|string|No comments found.|-
data|object|No comments found.|-
timestamp|string|No comments found.|-

**Response-example:**
```
{
  "code": 973,
  "msg": "42l2jz",
  "data": {
    "waring": "You may have used non-display generics."
  },
  "timestamp": "2022-10-25 11:01:31"
}
```

## 删除用户
**URL:** `http://127.0.0.1:4612/user/{id}`

**Type:** `DELETE`

**Author:** Dominick Li

**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 删除用户

**Request-headers:**

Header | Type|Description|Required|Since
---|---|---|---|----
token|string|身份凭证|false|-


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
id|string|用户Id|true|-



**Request-example:**
```
curl -X DELETE -H 'token' -i http://127.0.0.1:4612/user/83
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|No comments found.|-
msg|string|No comments found.|-
data|object|No comments found.|-
timestamp|string|No comments found.|-

**Response-example:**
```
{
  "code": 807,
  "msg": "cc3wrx",
  "data": {
    "waring": "You may have used non-display generics."
  },
  "timestamp": "2022-10-25 11:01:31"
}
```

