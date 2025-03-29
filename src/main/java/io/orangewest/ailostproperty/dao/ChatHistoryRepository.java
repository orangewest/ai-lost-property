package io.orangewest.ailostproperty.dao;

import io.orangewest.ailostproperty.pojo.entity.ChatHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatHistoryRepository extends BaseRepository<ChatHistoryEntity> {

    List<ChatHistoryEntity> findTop21BySessionIdOrderByIdDesc(String sessionId);

    Page<ChatHistoryEntity> findAllBySessionId(String sessionId, Pageable pageable);

    @Transactional
    @Modifying
    void deleteBySessionId(String userId);
}
