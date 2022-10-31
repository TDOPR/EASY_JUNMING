
# 用户管理
## 获取所有用户信息

**URL:** `http://127.0.0.1}/user`

**Type:** `GET`

**Author:** Dominick Li

**Content-Type:** `application/x-www-form-urlencoded;charset=UTF-8`

**Description:** 获取所有用户信息

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|





**Request-example:**
```
curl -X GET -H 'token' -i http://127.0.0.1:4612/user/
```

**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|成功标识 200成功，其它异常|-|
|msg|string|提示信息|-|
|data|array|数据|-|
|└─id|int32|唯一标识  修改时候需要该参数|-|
|└─username|string|用户名称|-|
|└─password|string|密码|-|
|└─age|int32|年龄|-|
|timestamp|string|响应时间戳|-|

**Response-example:**
```
{
  "code": 856,
  "msg": "rv4er4",
  "data": [
    {
      "id": 402,
      "createTime": "2022-10-25 11:14:48",
      "lastmodifiedTime": "2022-10-25 11:14:48",
      "username": "judith.friesen",
      "password": "mj56gm",
      "age": 51
    }
  ],
  "timestamp": "2022-10-25 11:14:48"
}
```

## 根据用户id获取用户信息

**URL:** `http://127.0.0.1}/user/{id}`

**Type:** `GET`

**Author:** Dominick Li

**Content-Type:** `application/x-www-form-urlencoded;charset=UTF-8`

**Description:** 根据用户id获取用户信息

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|


**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|id|string|true|用户Id|-|



**Request-example:**
```
curl -X GET -H 'token' -i http://127.0.0.1:4612/user/17
```

**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|成功标识 200成功，其它异常|-|
|msg|string|提示信息|-|
|data|object|数据|-|
|└─id|int32|唯一标识  修改时候需要该参数|-|
|└─username|string|用户名称|-|
|└─password|string|密码|-|
|└─age|int32|年龄|-|
|timestamp|string|响应时间戳|-|

**Response-example:**
```
{
  "code": 338,
  "msg": "i9n952",
  "data": {
    "id": 608,
    "createTime": "2022-10-25 11:14:48",
    "lastmodifiedTime": "2022-10-25 11:14:48",
    "username": "judith.friesen",
    "password": "2t33r7",
    "age": 51
  },
  "timestamp": "2022-10-25 11:14:48"
}
```

## 登录 入参方式: @RequestParam

**URL:** `http://127.0.0.1}/user/login`

**Type:** `POST`

**Author:** Dominick Li

**Content-Type:** `application/x-www-form-urlencoded;charset=UTF-8`

**Description:** 登录 入参方式: @RequestParam

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|



**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|username|string|true|用户名|-|
|password|string|true|密码|-|


**Request-example:**
```
curl -X POST -H 'token' -i http://127.0.0.1:4612/user/login --data 'username=judith.friesen&password=2uz9sl'
```

**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|成功标识 200成功，其它异常|-|
|msg|string|提示信息|-|
|data|object|数据|-|
|timestamp|string|响应时间戳|-|

**Response-example:**
```
{
  "code": 724,
  "msg": "w6nkh2",
  "data": {},
  "timestamp": "2022-10-25 11:14:48"
}
```

## 添加用户 入参方式: @RequestBody

**URL:** `http://127.0.0.1}/user`

**Type:** `POST`

**Author:** Dominick Li

**Content-Type:** `application/json`

**Description:** 添加用户 入参方式: @RequestBody

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|




**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|id|int32|false|唯一标识  修改时候需要该参数|-|
|username|string|true|用户名称|-|
|password|string|true|密码|-|
|age|int32|false|年龄|-|

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json' -H 'token' -i http://127.0.0.1:4612/user/ --data '{
  "id": 121,
  "username": "judith.friesen",
  "password": "01gyyn",
  "age": 51
}'
```

**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|成功标识 200成功，其它异常|-|
|msg|string|提示信息|-|
|data|object|数据|-|
|timestamp|string|响应时间戳|-|

**Response-example:**
```
{
  "code": 205,
  "msg": "lq0afn",
  "data": {},
  "timestamp": "2022-10-25 11:14:48"
}
```

## 删除用户

**URL:** `http://127.0.0.1}/user/{id}`

**Type:** `DELETE`

**Author:** Dominick Li

**Content-Type:** `application/x-www-form-urlencoded;charset=UTF-8`

**Description:** 删除用户

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|


**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|id|string|true|用户Id|-|



**Request-example:**
```
curl -X DELETE -H 'token' -i http://127.0.0.1:4612/user/17
```

**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|成功标识 200成功，其它异常|-|
|msg|string|提示信息|-|
|data|object|数据|-|
|timestamp|string|响应时间戳|-|

**Response-example:**
```
{
  "code": 426,
  "msg": "l1iu0o",
  "data": {},
  "timestamp": "2022-10-25 11:14:48"
}
```

