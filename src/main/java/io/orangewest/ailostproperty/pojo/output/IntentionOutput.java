package io.orangewest.ailostproperty.pojo.output;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class IntentionOutput {

    @Description("意图（1：丢失信息登记2：找到失物登记3：失物查询4：其他）")
    private Integer intention;

    @Description("大模型对用户的输出")
    private String output;

}
