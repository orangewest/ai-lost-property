package io.orangewest.ailostproperty.assistant.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import io.orangewest.ailostproperty.dao.LostPropertyRepository;
import io.orangewest.ailostproperty.pojo.entity.LostPropertyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LostPropertyTools {

    @Autowired
    private LostPropertyRepository lostPropertyRepository;

    @Autowired
    private ContentRetriever contentRetriever;

    @Tool("根据手机号查询物品登记信息")
    public List<LostPropertyEntity> queryLostPropertyByPhone(@P(value = "用户手机号") String phone) {
        log.info("根据手机号查询物品登记信息,手机号: {}", phone);
        return lostPropertyRepository.findAllByPhone(phone);
    }

    @Tool("根据物品及物品特征查询物品登记信息")
    public List<String> queryLostProperty(@P(value = "物品名称和特征") String lostProperty) {
        log.info("根据物品及物品特征查询物品登记信息,物品名称和特征: {}", lostProperty);
        List<String> list = contentRetriever.retrieve(new Query(lostProperty))
                .stream()
                .map(x -> x.textSegment().text())
                .toList();
        log.info("根据物品及物品特征查询物品登记信息,查询结果: {}", list);
        return list;
    }

}
