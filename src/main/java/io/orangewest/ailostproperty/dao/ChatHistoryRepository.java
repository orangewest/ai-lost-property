package io.orangewest.ailostproperty.dao;

import io.orangewest.ailostproperty.pojo.entity.ChatHistoryEntity;

import java.util.List;

public interface ChatHistoryRepository extends BaseRepository<ChatHistoryEntity> {

    List<ChatHistoryEntity> findTop21BySessionIdOrderByIdDesc(String sessionId);
}
