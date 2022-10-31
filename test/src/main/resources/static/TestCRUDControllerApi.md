
# CRUD通用功能测试
## 分页查询

**URL:** `http://127.0.0.1}/test`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=UTF-8`

**Description:** 分页查询

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|



**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|currentPage|int32|true|当前页|-|
|pageSize|int32|true|每页显示的数量|-|
|beginDate|string|false|日期查询条件起始|-|
|endDate|string|false|日期查询条件结束|-|
|eq|map|false|等值查询条件|-|
|└─any object|object|false|any object.|-|
|like|map|false|模糊查询条件|-|


**Request-example:**
```
curl -X GET -H 'token' -i http://127.0.0.1:4612/test/?currentPage=1&pageSize=10&beginDate=2022-10-25&endDate=2022-10-25
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
  "code": 132,
  "msg": "et7l25",
  "data": {},
  "timestamp": "2022-10-25 11:14:48"
}
```

## 根据Id查询

**URL:** `http://127.0.0.1}/test/{id}`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=UTF-8`

**Description:** 根据Id查询

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|


**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|id|int32|true|用户Id|-|



**Request-example:**
```
curl -X GET -H 'token' -i http://127.0.0.1:4612/test/397
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
  "code": 261,
  "msg": "nfwiod",
  "data": {},
  "timestamp": "2022-10-25 11:14:48"
}
```

## 保存数据

**URL:** `http://127.0.0.1}/test`

**Type:** `POST`


**Content-Type:** `application/json`

**Description:** 保存数据

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|




**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|id|int32|false|唯一标识  修改时候需要该参数|-|
|name|string|false|No comments found.|-|
|password|string|false|No comments found.|-|

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json' -H 'token' -i http://127.0.0.1:4612/test/ --data '{
  "id": 498,
  "name": "judith.friesen",
  "password": "z2pgt6"
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
  "code": 809,
  "msg": "89n116",
  "data": {},
  "timestamp": "2022-10-25 11:14:48"
}
```

## 删除数据

**URL:** `http://127.0.0.1}/test/deletebyIds`

**Type:** `POST`


**Content-Type:** `application/json`

**Description:** 删除数据

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|token|string|false|身份凭证|-|




**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|idList|array|false|唯一标识数组|-|

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json' -H 'token' -i http://127.0.0.1:4612/test/deletebyIds --data '{
  "idList": [
    577
  ]
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
  "code": 60,
  "msg": "joh52k",
  "data": {},
  "timestamp": "2022-10-25 11:14:48"
}
```

