package cn.boommanpro.gaia.workflow.demo;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;

import java.util.HashMap;
import java.util.Map;

/**
 * 循环节点演示程序
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
public class LoopDemo {

    public static void main(String[] args) {
        System.out.println("=== 循环节点演示 ===");

        // 创建包含循环节点的工作流JSON
        String workflowJson = "{\n" +
                "  \"nodes\": [\n" +
                "    {\n" +
                "      \"id\": \"start_0\",\n" +
                "      \"type\": \"start\",\n" +
                "      \"meta\": {\n" +
                "        \"position\": {\n" +
                "          \"x\": 180,\n" +
                "          \"y\": 262.1\n" +
                "        }\n" +
                "      },\n" +
                "      \"data\": {\n" +
                "        \"title\": \"开始节点\",\n" +
                "        \"outputs\": {\n" +
                "          \"type\": \"object\",\n" +
                "          \"properties\": {\n" +
                "            \"array_obj\": {\n" +
                "              \"type\": \"array\",\n" +
                "              \"items\": {\n" +
                "                \"type\": \"string\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"loop_TC60x\",\n" +
                "      \"type\": \"loop\",\n" +
                "      \"meta\": {\n" +
                "        \"position\": {\n" +
                "          \"x\": 540,\n" +
                "          \"y\": 0\n" +
                "        }\n" +
                "      },\n" +
                "      \"data\": {\n" +
                "        \"title\": \"Loop_1\",\n" +
                "        \"loopFor\": {\n" +
                "          \"type\": \"ref\",\n" +
                "          \"content\": [\n" +
                "            \"start_0\",\n" +
                "            \"array_obj\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"loopOutputs\": {\n" +
                "          \"user\": {\n" +
                "            \"type\": \"ref\",\n" +
                "            \"content\": [\n" +
                "              \"216606\",\n" +
                "              \"result\"\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        \"outputs\": {\n" +
                "          \"type\": \"object\",\n" +
                "          \"required\": [],\n" +
                "          \"properties\": {\n" +
                "            \"user\": {\n" +
                "              \"type\": \"array\",\n" +
                "              \"items\": {\n" +
                "                \"type\": \"string\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"blocks\": [\n" +
                "          {\n" +
                "            \"id\": \"block_start_O0kbZ\",\n" +
                "            \"type\": \"block-start\",\n" +
                "            \"meta\": {\n" +
                "              \"position\": {\n" +
                "                \"x\": 32,\n" +
                "                \"y\": 190.6\n" +
                "              }\n" +
                "            },\n" +
                "            \"data\": {}\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": \"block_end_CDMp3\",\n" +
                "            \"type\": \"block-end\",\n" +
                "            \"meta\": {\n" +
                "              \"position\": {\n" +
                "                \"x\": 656,\n" +
                "                \"y\": 190.6\n" +
                "              }\n" +
                "            },\n" +
                "            \"data\": {}\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": \"216606\",\n" +
                "            \"type\": \"code\",\n" +
                "            \"meta\": {\n" +
                "              \"position\": {\n" +
                "                \"x\": 344,\n" +
                "                \"y\": 0\n" +
                "              }\n" +
                "            },\n" +
                "            \"data\": {\n" +
                "              \"title\": \"Code_1\",\n" +
                "              \"code\": \"return \\\"User_\\\" + loop.index + \\\"_\\\" + loop.item;\",\n" +
                "              \"inputs\": {\n" +
                "                \"type\": \"object\",\n" +
                "                \"properties\": {\n" +
                "                  \"loop\": {\n" +
                "                    \"type\": \"object\"\n" +
                "                  }\n" +
                "                }\n" +
                "              },\n" +
                "              \"outputs\": {\n" +
                "                \"type\": \"object\",\n" +
                "                \"properties\": {\n" +
                "                  \"result\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"edges\": [\n" +
                "          {\n" +
                "            \"sourceNodeID\": \"block_start_O0kbZ\",\n" +
                "            \"targetNodeID\": \"216606\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"sourceNodeID\": \"216606\",\n" +
                "            \"targetNodeID\": \"block_end_CDMp3\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"end_0\",\n" +
                "      \"type\": \"end\",\n" +
                "      \"meta\": {\n" +
                "        \"position\": {\n" +
                "          \"x\": 2344,\n" +
                "          \"y\": 262.1\n" +
                "        }\n" +
                "      },\n" +
                "      \"data\": {\n" +
                "        \"title\": \"结束节点\",\n" +
                "        \"inputsValues\": {\n" +
                "          \"success\": {\n" +
                "            \"type\": \"constant\",\n" +
                "            \"content\": true,\n" +
                "            \"schema\": {\n" +
                "              \"type\": \"boolean\"\n" +
                "            }\n" +
                "          },\n" +
                "          \"loop_user\": {\n" +
                "            \"type\": \"ref\",\n" +
                "            \"content\": [\n" +
                "              \"loop_TC60x\",\n" +
                "              \"user\"\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        \"inputs\": {\n" +
                "          \"type\": \"object\",\n" +
                "          \"properties\": {\n" +
                "            \"success\": {\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            \"loop_user\": {\n" +
                "              \"type\": \"array\",\n" +
                "              \"items\": {\n" +
                "                \"type\": \"string\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    {\n" +
                "      \"sourceNodeID\": \"start_0\",\n" +
                "      \"targetNodeID\": \"loop_TC60x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"sourceNodeID\": \"loop_TC60x\",\n" +
                "      \"targetNodeID\": \"end_0\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            // 创建工作流实例
            GaiaWorkflow workflow = new GaiaWorkflow(workflowJson);

            // 准备输入数据
            Map<String, Object> inputs = new HashMap<>();

            System.out.println("开始执行循环工作流...");

            // 执行工作流
            Map<String, Object> result = workflow.run(inputs);

            System.out.println("\n=== 执行结果 ===");
            System.out.println("整体结果: " + result);

            // 显示循环输出
            if (result.containsKey("end_0")) {
                Map<String, Object> endResult = (Map<String, Object>) result.get("end_0");
                if (endResult.containsKey("loop_user")) {
                    Object loopUsers = endResult.get("loop_user");
                    System.out.println("循环输出用户列表: " + loopUsers);

                    if (loopUsers instanceof java.util.List) {
                        java.util.List<String> users = (java.util.List<String>) loopUsers;
                        System.out.println("用户数量: " + users.size());
                        for (int i = 0; i < users.size(); i++) {
                            System.out.println("  用户 " + i + ": " + users.get(i));
                        }
                    }
                }
            }

            System.out.println("\n=== 演示完成 ===");

        } catch (Exception e) {
            System.err.println("执行工作流时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

