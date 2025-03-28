package io.orangewest.ailostproperty.pojo.entity;

import io.orangewest.ailostproperty.pojo.enums.ChatRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

@EqualsAndHashCode(callSuper = true)
@Table(name = "chat_history")
@Entity
@Data
@Comment("聊天历史")
public class ChatHistoryEntity extends BaseEntity {

    @Comment("会话id")
    private String sessionId;

    @Comment("角色")
    private ChatRole role;

    @Comment("内容")
    private String content;

}
