## 操作符和数据格式表

| 操作符 | 枚举值 | 描述 | 简写 | 适用数据类型 |
|--------|--------|------|------|-------------|
| Equal | `eq` | 等于 | = | string, number, integer, boolean, array |
| Not Equal | `neq` | 不等于 | ≠ | string, number, integer, boolean, array |
| Greater Than | `gt` | 大于 | > | number, integer |
| Greater Than or Equal | `gte` | 大于等于 | >= | number, integer |
| Less Than | `lt` | 小于 | < | number, integer |
| Less Than or Equal | `lte` | 小于等于 | <= | number, integer |
| In | `in` | 在...之中 | ∈ | string, number, integer, boolean |
| Not In | `nin` | 不在...之中 | ∉ | string, number, integer, boolean |
| Contains | `contains` | 包含 | ⊇ | string, array |
| Not Contains | `not_contains` | 不包含 | ⊉ | string, array |
| Is Empty | `is_empty` | 为空 | = | string, object, array, map |
| Is Not Empty | `is_not_empty` | 不为空 | ≠ | string, object, array, map |
| Is True | `is_true` | 为真 | = | boolean |
| Is False | `is_false` | 为假 | = | boolean |

### 数据类型支持的操作符

| 数据类型 | 支持的操作符 |
|----------|-------------|
| string | `eq`, `neq`, `contains`, `not_contains`, `in`, `nin`, `is_empty`, `is_not_empty` |
| number | `eq`, `neq`, `gt`, `gte`, `lt`, `lte`, `in`, `nin` |
| integer | `eq`, `neq`, `gt`, `gte`, `lt`, `lte`, `in`, `nin` |
| boolean | `eq`, `neq`, `is_true`, `is_false`, `in`, `nin` |
| object | `is_empty`, `is_not_empty` |
| array | `is_empty`, `is_not_empty`, `contains`, `not_contains`, `eq`, `neq` |
| map | `is_empty`, `is_not_empty` |
