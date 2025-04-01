package io.orangewest.ailostproperty.dao;

import io.orangewest.ailostproperty.pojo.entity.LostPropertyEntity;

import java.util.List;

public interface LostPropertyRepository extends BaseRepository<LostPropertyEntity> {

    List<LostPropertyEntity> findAllByPhone(String phone);

}
