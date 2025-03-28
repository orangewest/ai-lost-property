package io.orangewest.ailostproperty.dao;

import io.orangewest.ailostproperty.pojo.entity.BaseEntity;
import org.springframework.data.repository.CrudRepository;

public interface BaseRepository<T extends BaseEntity> extends CrudRepository<T,Long> {
}
