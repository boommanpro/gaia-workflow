# 工作流后台管理系统API文档

## 1. 模板管理接口

### 1.1 获取模板列表
```
GET /api/template/list
```
**说明**: 获取所有模板列表

**响应示例**:
```json
[
  {
    "id": 1,
    "templateCode": "template_001",
    "templateName": "审批流程模板",
    "templateDesc": "通用审批流程模板",
    "templateData": "{\"nodes\":[],\"edges\":[]}",
    "createdAt": "2023-01-01T12:00:00",
    "updatedAt": "2023-01-01T12:00:00"
  }
]
```

### 1.2 根据ID获取模板详情
```
GET /api/template/{id}
```
**说明**: 根据ID获取模板详情

**路径参数**:
- id: 模板ID

### 1.3 创建新模板
```
POST /api/template/create
```
**说明**: 创建新模板

**请求体**:
```json
{
  "templateCode": "template_001",
  "templateName": "审批流程模板",
  "templateDesc": "通用审批流程模板",
  "templateData": "{\"nodes\":[],\"edges\":[]}"
}
```

### 1.4 更新模板
```
PUT /api/template/update
```
**说明**: 更新模板

**请求体**:
```json
{
  "id": 1,
  "templateCode": "template_001",
  "templateName": "审批流程模板",
  "templateDesc": "通用审批流程模板",
  "templateData": "{\"nodes\":[],\"edges\":[]}"
}
```

### 1.5 删除模板
```
DELETE /api/template/delete/{id}
```
**说明**: 删除模板

**路径参数**:
- id: 模板ID

## 2. 工作流管理接口

### 2.1 获取工作流列表
```
GET /api/workflow/list
```
**说明**: 获取所有工作流列表

**响应示例**:
```json
[
  {
    "id": 1,
    "workflowCode": "workflow_001",
    "workflowName": "请假审批流程",
    "workflowDesc": "员工请假审批流程",
    "currentVersionId": 1,
    "templateCode": "template_001",
    "createdAt": "2023-01-01T12:00:00",
    "updatedAt": "2023-01-01T12:00:00"
  }
]
```

### 2.2 根据ID获取工作流详情
```
GET /api/workflow/{id}
```
**说明**: 根据ID获取工作流详情

**路径参数**:
- id: 工作流ID

### 2.3 根据工作流编码获取工作流详情
```
GET /api/workflow/code/{workflowCode}
```
**说明**: 根据工作流编码获取工作流详情

**路径参数**:
- workflowCode: 工作流编码

### 2.4 创建新工作流
```
POST /api/workflow/create
```
**说明**: 创建新工作流

**请求体**:
```json
{
  "workflowCode": "workflow_001",
  "workflowName": "请假审批流程",
  "workflowDesc": "员工请假审批流程",
  "currentVersionId": 1,
  "templateCode": "template_001"
}
```

### 2.5 更新工作流
```
PUT /api/workflow/update
```
**说明**: 更新工作流

**请求体**:
```json
{
  "id": 1,
  "workflowCode": "workflow_001",
  "workflowName": "请假审批流程",
  "workflowDesc": "员工请假审批流程",
  "currentVersionId": 1,
  "templateCode": "template_001"
}
```

### 2.6 删除工作流
```
DELETE /api/workflow/delete/{id}
```
**说明**: 删除工作流

**路径参数**:
- id: 工作流ID

## 3. 工作流版本管理接口

### 3.1 获取指定工作流的所有版本列表
```
GET /api/workflow-version/list/{workflowCode}
```
**说明**: 获取指定工作流的所有版本列表

**路径参数**:
- workflowCode: 工作流编码

**响应示例**:
```json
[
  {
    "id": 1,
    "workflowCode": "workflow_001",
    "versionNumber": "v1.0",
    "versionDesc": "初始版本",
    "workflowData": "{\"nodes\":[],\"edges\":[]}",
    "createdBy": "admin",
    "createdAt": "2023-01-01T12:00:00",
    "isCurrent": 1
  }
]
```

### 3.2 根据ID获取版本详情
```
GET /api/workflow-version/{id}
```
**说明**: 根据ID获取版本详情

**路径参数**:
- id: 版本ID

### 3.3 创建新版本
```
POST /api/workflow-version/create
```
**说明**: 创建新版本

**请求体**:
```json
{
  "workflowCode": "workflow_001",
  "versionNumber": "v1.0",
  "versionDesc": "初始版本",
  "workflowData": "{\"nodes\":[],\"edges\":[]}",
  "createdBy": "admin",
  "isCurrent": 1
}
```

### 3.4 更新版本
```
PUT /api/workflow-version/update
```
**说明**: 更新版本

**请求体**:
```json
{
  "id": 1,
  "workflowCode": "workflow_001",
  "versionNumber": "v1.0",
  "versionDesc": "初始版本",
  "workflowData": "{\"nodes\":[],\"edges\":[]}",
  "createdBy": "admin",
  "isCurrent": 1
}
```

### 3.5 删除版本
```
DELETE /api/workflow-version/delete/{id}
```
**说明**: 删除版本

**路径参数**:
- id: 版本ID

### 3.6 设置为当前版本
```
PUT /api/workflow-version/set-current/{id}
```
**说明**: 设置为当前版本

**路径参数**:
- id: 版本ID

## 4. 工作流日志管理接口

### 4.1 获取指定工作流的所有日志列表
```
GET /api/workflow-log/list/{workflowCode}
```
**说明**: 获取指定工作流的所有日志列表

**路径参数**:
- workflowCode: 工作流编码

**响应示例**:
```json
[
  {
    "id": 1,
    "workflowCode": "workflow_001",
    "versionNumber": "v1.0",
    "executionId": "exec_001",
    "startTime": "2023-01-01T12:00:00",
    "endTime": "2023-01-01T12:05:00",
    "status": "success",
    "inputParams": "{\"employeeId\":\"emp001\",\"days\":3}",
    "outputParams": "{\"approved\":true}",
    "errorMessage": "",
    "executionDuration": 300000,
    "createdAt": "2023-01-01T12:05:00"
  }
]
```

### 4.2 获取指定工作流和版本的日志列表
```
GET /api/workflow-log/list/{workflowCode}/{versionNumber}
```
**说明**: 获取指定工作流和版本的日志列表

**路径参数**:
- workflowCode: 工作流编码
- versionNumber: 版本号

### 4.3 根据ID获取日志详情
```
GET /api/workflow-log/{id}
```
**说明**: 根据ID获取日志详情

**路径参数**:
- id: 日志ID

### 4.4 根据执行ID获取日志详情
```
GET /api/workflow-log/execution/{executionId}
```
**说明**: 根据执行ID获取日志详情

**路径参数**:
- executionId: 执行ID

### 4.5 创建新日志
```
POST /api/workflow-log/create
```
**说明**: 创建新日志

**请求体**:
```json
{
  "workflowCode": "workflow_001",
  "versionNumber": "v1.0",
  "executionId": "exec_001",
  "startTime": "2023-01-01T12:00:00",
  "endTime": "2023-01-01T12:05:00",
  "status": "success",
  "inputParams": "{\"employeeId\":\"emp001\",\"days\":3}",
  "outputParams": "{\"approved\":true}",
  "errorMessage": "",
  "executionDuration": 300000
}
```

### 4.6 更新日志
```
PUT /api/workflow-log/update
```
**说明**: 更新日志

**请求体**:
```json
{
  "id": 1,
  "workflowCode": "workflow_001",
  "versionNumber": "v1.0",
  "executionId": "exec_001",
  "startTime": "2023-01-01T12:00:00",
  "endTime": "2023-01-01T12:05:00",
  "status": "success",
  "inputParams": "{\"employeeId\":\"emp001\",\"days\":3}",
  "outputParams": "{\"approved\":true}",
  "errorMessage": "",
  "executionDuration": 300000
}
```

### 4.7 删除日志
```
DELETE /api/workflow-log/delete/{id}
```
**说明**: 删除日志

**路径参数**:
- id: 日志ID