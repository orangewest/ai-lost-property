package io.orangewest.ailostproperty.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ChatHistory {


    private String role;

    private String content;

}
