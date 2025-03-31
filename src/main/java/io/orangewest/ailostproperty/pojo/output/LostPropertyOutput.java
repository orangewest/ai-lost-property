package io.orangewest.ailostproperty.pojo.output;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class LostPropertyOutput {

    @Description("大模型对用户的输出")
    private String output;

    @Description("用户姓名")
    private String username;

    @Description("用户手机号")
    private String phone;

    @Description("失物名称")
    private String lostName;

    @Description("失物特征")
    private String lostType;

    @Description("是否完成登记")
    private Boolean completed;

    @Description("数据库记录id")
    private Long id;

}
