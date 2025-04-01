package io.orangewest.ailostproperty.pojo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

@EqualsAndHashCode(callSuper = true)
@Table(name = "lost_property")
@Entity
@Data
@Comment("失物表")
public class LostPropertyEntity extends BaseEntity {

    @Comment("捡到失物的用户姓名")
    private String username;

    @Comment("捡到失物的用户手机号")
    private String phone;

    @Comment("失物名称")
    private String lostName;

    @Comment("失物特征")
    private String lostType;

}
