package com.card.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.card.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RolePermissionDao extends BaseMapper<RolePermission> {
}
