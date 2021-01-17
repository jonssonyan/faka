package com.card.controller;

import com.card.entity.RolePermission;
import com.card.entity.vo.Result;
import com.card.entity.vo.RolePermissionVO;
import com.card.service.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/rolePermission")
public class RolePermissionController {
    @Autowired
    private RolePermissionService rolePermissionService;

    @PostMapping("/saveOrUpdate")
    @RequiresPermissions({"rolePermission:add"})
    @RequiresRoles({"admin"})
    public Result<Object> saveOrUpdate(@RequestBody RolePermission rolePermission) {
        Integer count = rolePermissionService.lambdaQuery().eq(RolePermission::getRoleId, rolePermission.getRoleId())
                .eq(RolePermission::getPermissionId, rolePermission.getPermissionId()).count();
        if (count > 0) return Result.fail("授权已存在");
        rolePermissionService.saveOrUpdate(rolePermission);
        return Result.success();
    }

    @PostMapping("/removeById")
    @RequiresPermissions({"rolePermission:delete"})
    @RequiresRoles({"admin"})
    public Result<Object> removeById(@RequestBody RolePermissionVO rolePermissionVO) {
        rolePermissionService.removeById(rolePermissionVO.getId());
        return Result.success();
    }

    @PostMapping("/selectPage")
    @RequiresRoles({"admin"})
    @RequiresPermissions({"rolePermission:select"})
    public Result<Object> selectPage(@RequestBody RolePermissionVO rolePermissionVO) {
        return Result.success(rolePermissionService.selectPage(rolePermissionVO));
    }
}
