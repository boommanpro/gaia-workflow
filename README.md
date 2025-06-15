## Workflow Server


workflow 简单来说就是一个低代码函数编写，所以看起来会比自己编写更麻烦，他需要更严格的定义

举例
函数的入参 start节点  inputs

函数的返回值 end节点  outputs

入参需要具备

入参的类型、默认值、描述。其中类型需要支持多种。

对于开始节点是不需要支持引用的，对于其余所有节点的入参都需要支持引用。

对于code节点的code执行仅能使用具备变量，不能使用全局变量。

```
        outputs: {
          type: 'object',
          properties: {
            query: {
              type: 'string',
              default: 'Hello Flow.',
            },
            enable: {
              type: 'boolean',
              default: true,
            },
            array_obj: {
              type: 'array',
              items: {
                type: 'object',
                properties: {
                  int: {
                    type: 'number',
                  },
                  str: {
                    type: 'string',
                  },
                },
              },
            },
          },
        }
```
对于input节点和output节点都需要支持inputsValues,代表默认值能力
