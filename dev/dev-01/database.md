# 工作流后台管理系统数据库设计

## 1. 模板管理表 (gaia_workflow_template)

用于存储工作流模板信息。

```sql
CREATE TABLE gaia_workflow_template (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    template_code VARCHAR(64) NOT NULL UNIQUE COMMENT '模板编码',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    template_desc TEXT COMMENT '模板描述',
    template_data TEXT COMMENT '模板数据（JSON格式）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）'
);
```

## 2. 工作流表 (gaia_workflow)

用于存储工作流基本信息。

```sql
CREATE TABLE gaia_workflow (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    workflow_code VARCHAR(64) NOT NULL UNIQUE COMMENT '工作流编码，全局唯一',
    workflow_name VARCHAR(128) NOT NULL COMMENT '工作流名称',
    workflow_desc TEXT COMMENT '工作流描述',
    current_version_id INTEGER COMMENT '当前版本ID',
    template_code VARCHAR(64) COMMENT '来源模板编码',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）'
);
```

## 3. 工作流版本表 (gaia_workflow_version)

用于存储工作流各个版本的信息。

```sql
CREATE TABLE gaia_workflow_version (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    workflow_code VARCHAR(64) NOT NULL COMMENT '工作流编码',
    version_number VARCHAR(32) NOT NULL COMMENT '版本号',
    version_desc VARCHAR(256) COMMENT '版本描述',
    workflow_data TEXT COMMENT '工作流数据（JSON格式）',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_current TINYINT DEFAULT 0 COMMENT '是否为当前版本（0-否，1-是）',
    UNIQUE(workflow_code, version_number)
);
```

## 4. 工作流日志表 (gaia_workflow_log)

用于记录工作流执行日志。

```sql
CREATE TABLE gaia_workflow_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    workflow_code VARCHAR(64) NOT NULL COMMENT '工作流编码',
    version_number VARCHAR(32) NOT NULL COMMENT '版本号',
    execution_id VARCHAR(64) NOT NULL UNIQUE COMMENT '执行ID，全局唯一',
    start_time DATETIME COMMENT '开始执行时间',
    end_time DATETIME COMMENT '执行结束时间',
    status VARCHAR(32) COMMENT '执行状态（success-成功，failed-失败，running-执行中）',
    input_params TEXT COMMENT '输入参数（JSON格式）',
    output_params TEXT COMMENT '输出参数（JSON格式）',
    error_message TEXT COMMENT '错误信息',
    execution_duration BIGINT COMMENT '执行时长（毫秒）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);
```

## 表关系说明

1. `gaia_workflow_template` 表存储所有可用的工作流模板，用于创建工作流时的选择
2. `gaia_workflow` 表存储工作流的基本信息，每个工作流通过 `workflow_code` 唯一标识
3. `gaia_workflow_version` 表存储工作流的所有版本，一个工作流可以有多个版本
4. `gaia_workflow_log` 表记录工作流的执行日志，与特定的工作流及其版本关联

## 索引优化建议

```sql
-- 为提高查询效率，建议添加以下索引
CREATE INDEX idx_workflow_code ON gaia_workflow(workflow_code);
CREATE INDEX idx_workflow_version_code ON gaia_workflow_version(workflow_code);
CREATE INDEX idx_workflow_version_current ON gaia_workflow_version(workflow_code, is_current);
CREATE INDEX idx_workflow_log_code ON gaia_workflow_log(workflow_code);
CREATE INDEX idx_workflow_log_execution ON gaia_workflow_log(execution_id);
```