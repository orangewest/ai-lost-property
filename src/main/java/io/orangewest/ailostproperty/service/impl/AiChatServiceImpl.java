package io.orangewest.ailostproperty.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.orangewest.ailostproperty.assistant.AiAssistant;
import io.orangewest.ailostproperty.assistant.LostPropertyAssistant;
import io.orangewest.ailostproperty.component.ChatFlow;
import io.orangewest.ailostproperty.dao.ChatHistoryRepository;
import io.orangewest.ailostproperty.dao.LostPropertyRepository;
import io.orangewest.ailostproperty.dao.LostRegisterRepository;
import io.orangewest.ailostproperty.pojo.dto.ChatHistoryVo;
import io.orangewest.ailostproperty.pojo.entity.LostPropertyEntity;
import io.orangewest.ailostproperty.pojo.entity.LostRegisterEntity;
import io.orangewest.ailostproperty.pojo.output.IntentionOutput;
import io.orangewest.ailostproperty.pojo.output.LostPropertyOutput;
import io.orangewest.ailostproperty.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiAssistant aiAssistant;

    @Autowired
    private LostRegisterRepository lostRegisterRepository;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private LostPropertyAssistant lostPropertyAssistant;

    @Autowired
    private LostPropertyRepository lostPropertyRepository;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Override
    @ChatFlow
    public String chat(String userId, String message) {
        // 判断用户的意图
        IntentionOutput intention = aiAssistant.getIntention(userId, message);
        log.info("intention:{}", intention);
        String output = intention.getOutput();
        switch (intention.getIntention()) {
            case 1:
                // 丢失信息登记
                output = registerLost(userId, message);
                break;
            case 2:
                // 找到失物登记
                output = registerLostProperty(userId, message);
                break;
            case 3:
                // 失物查询
                output = queryLostProperty(userId, message);
                break;
        }
        return output;
    }

    private String queryLostProperty(String userId, String message) {
        StringBuilder sb = new StringBuilder();
        lostPropertyAssistant.queryLostProperty(userId, message)
                .doOnNext(sb::append)
                .blockLast();
        log.info("queryLostProperty:{}", sb);
        return sb.toString();
    }

    @Override
    public Page<ChatHistoryVo> queryChatHistory(String userId, Pageable page) {
        return chatHistoryRepository.findAllBySessionId(userId, page)
                .map(chatHistory -> ChatHistoryVo.of(chatHistory.getRole().getRole(), chatHistory.getContent(), chatHistory.getCreatedDate()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearChatHistory(String userId) {
        chatHistoryRepository.deleteBySessionId(userId);
    }

    @Override
    public String embeddingIndex() {
        String path = "./data/data.txt";
        try {
            // 将丢失物品数据写入文件
            FileUtil.writeString("", path, StandardCharsets.UTF_8);
            List<String> collect = StreamSupport.stream(lostPropertyRepository.findAll().spliterator(), false)
                    .map(JsonUtils::toJson)
                    .toList();
            FileUtil.appendLines(collect, path, StandardCharsets.UTF_8);
            log.info("write file success");
        } catch (Exception e) {
            log.error("write file error", e);
        }

        // 加载并解析文档内容
        DocumentParser documentParser = new TextDocumentParser();
        Document document = FileSystemDocumentLoader.loadDocument(FileUtil.getAbsolutePath(path), documentParser);

        // 按行分割文档为指定长度的文本片段
        DocumentSplitter splitter = new DocumentByLineSplitter(200, 100);
        List<TextSegment> documents = splitter.split(document);

        // 生成文本片段的嵌入向量并存储
        List<Embedding> embeddings = embeddingModel.embedAll(documents).content();
        embeddingStore.addAll(embeddings, documents);

        return "success";
    }

    @Override
    public List<String> embeddingQuery(String message) {
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(embeddingModel.embed(message).content())
                .minScore(0.5)
                .maxResults(2)
                .build();
        EmbeddingSearchResult<TextSegment> search = embeddingStore.search(embeddingSearchRequest);
        return search.matches().stream().map(x -> x.embedded().text()).collect(Collectors.toList());
    }


    private String registerLost(String userId, String message) {
        LostPropertyOutput lostPropertyOutput = aiAssistant.registerLost(userId, message);
        log.info("lostPropertyOutput:{}", lostPropertyOutput);
        if (lostPropertyOutput.getCompleted()) {
            LostRegisterEntity lostRegisterEntity = new LostRegisterEntity();
            BeanUtils.copyProperties(lostPropertyOutput, lostRegisterEntity);
            lostRegisterRepository.save(lostRegisterEntity);
        }
        return lostPropertyOutput.getOutput();
    }

    private String registerLostProperty(String userId, String message) {
        StringBuilder sb = new StringBuilder();
        lostPropertyAssistant.registerLostProperty(userId, message)
                .doOnNext(sb::append)
                .blockLast();
        log.info("registerLostProperty sb:{}", sb);
        LostPropertyOutput lostPropertyOutput = JsonUtils.fromJson(sb.toString(), LostPropertyOutput.class);
        if (lostPropertyOutput.getCompleted()) {
            LostPropertyEntity lostPropertyEntity = new LostPropertyEntity();
            BeanUtils.copyProperties(lostPropertyOutput, lostPropertyEntity);
            lostPropertyRepository.save(lostPropertyEntity);
        }
        return lostPropertyOutput.getOutput();
    }

}
