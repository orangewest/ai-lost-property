package io.orangewest.ailostproperty.dao;

import io.orangewest.ailostproperty.pojo.entity.LostRegisterEntity;

import java.util.List;

public interface LostRegisterRepository extends BaseRepository<LostRegisterEntity> {
    
    List<LostRegisterEntity> findAllByPhone(String phone);

}
