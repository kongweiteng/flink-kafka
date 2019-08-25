package flink;

import org.apache.flink.api.java.utils.ParameterTool;

import java.io.IOException;

public class PropertUtil {

    static String propertiesFile = "src/main/resources/env.properties";
    static ParameterTool parameter;


    static {
        {
            try {
                parameter = ParameterTool.fromPropertiesFile(propertiesFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * 获取配置信息
     */
    public static ParameterTool getParamTool() {
        return parameter;
    }


    /**
     * 根据配置key获取value
     */
    public static String getPropertValue(String key) {
        return getParamTool().get(key);
    }

}
