package flink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class MessageSplitter implements FlatMapFunction<String, Tuple2<String, Long>> {
    @Override
    public void flatMap(String value, Collector<Tuple2<String, Long>> out) throws Exception {
        JSONObject valueObj = JSON.parseObject(value);
        JSONObject msg = valueObj.getJSONObject("msg");
        System.err.println(JSON.toJSONString(msg));
//        if (value != null && value.contains(",")) {
//            String[] parts = value.split(",");
//            out.collect(new Tuple2<>(parts[1], Long.parseLong(parts[2])));
//        }
    }
}
