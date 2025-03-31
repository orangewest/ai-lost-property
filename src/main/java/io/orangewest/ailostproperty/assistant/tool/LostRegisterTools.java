package io.orangewest.ailostproperty.assistant.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import io.orangewest.ailostproperty.dao.LostRegisterRepository;
import io.orangewest.ailostproperty.pojo.entity.LostRegisterEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LostRegisterTools {

    @Autowired
    private LostRegisterRepository lostRegisterRepository;

    @Tool("根据手机号查询丢失登记信息")
    public List<LostRegisterEntity> queryLostRegisterByPhone(@P(value = "用户手机号") String phone) {
        log.info("根据手机号查询丢失登记信息,手机号: {}", phone);
        return lostRegisterRepository.findAllByPhone(phone);
    }

}
