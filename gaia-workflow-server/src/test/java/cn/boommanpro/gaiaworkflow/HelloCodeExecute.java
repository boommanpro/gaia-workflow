package cn.boommanpro.gaiaworkflow;

import cn.boommanpro.gaia.workflow.code.CodeExecute;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloCodeExecute implements CodeExecute {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        HashMap<String, Object> result = new HashMap<>();

        try {
            // 获取user数组
            Object userObj = inputs.get("user");
            if (userObj instanceof List) {
                List<String> userStrings = (List<String>) userObj;
                List<Object> cleanedUsers = new ArrayList<>();

                for (String userStr : userStrings) {
                    try {
                        // 清洗数据：去除```json和```标记
                        String cleanedStr = cleanJsonString(userStr);

                        // 解析JSON字符串为对象
                        Object userObject = objectMapper.readValue(cleanedStr, Object.class);
                        cleanedUsers.add(userObject);
                    } catch (Exception e) {
                        // 如果解析失败，可以记录错误或跳过
                        System.err.println("Failed to parse user string: " + userStr);
                        e.printStackTrace();
                    }
                }

                result.put("cleaned_users", cleanedUsers);
            }
        } catch (Exception e) {
            System.err.println("Error processing user data: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 清洗JSON字符串，去除```json和```标记
     */
    private String cleanJsonString(String jsonString) {
        if (jsonString == null) {
            return "";
        }

        // 去除开头的```json
        if (jsonString.startsWith("```json")) {
            jsonString = jsonString.substring(7).trim();
        }

        // 去除结尾的```
        if (jsonString.endsWith("```")) {
            jsonString = jsonString.substring(0, jsonString.length() - 3).trim();
        }

        return jsonString;
    }
}
