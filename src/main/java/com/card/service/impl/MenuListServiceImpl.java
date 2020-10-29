package com.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.card.dao.CardDao;
import com.card.dao.MenuListDao;
import com.card.entity.Card;
import com.card.entity.MenuList;
import com.card.entity.vo.MenuListVO;
import com.card.service.MenuListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MenuListServiceImpl extends ServiceImpl<MenuListDao, MenuList> implements MenuListService {
    @Autowired
    private MenuListDao menuListDao;

    @Override
    public IPage<MenuList> selectPage(MenuListVO menuListVO) {
        Page<MenuList> menuListPage = new Page<>(menuListVO.getPageNum(), menuListVO.getPageSize());
        QueryWrapper<MenuList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(null != menuListVO.getRoleId(), "role_id", menuListVO.getRoleId());
        queryWrapper.eq(null != menuListVO.getParentId(), "parent_id", menuListVO.getParentId());
        queryWrapper.like(StringUtils.isNotBlank(menuListVO.getName()), "name", menuListVO.getName());
        queryWrapper.eq(null != menuListVO.getState(), "state", menuListVO.getState());
        queryWrapper.between(null != menuListVO.getStartTime() && null != menuListVO.getEndTime(), "create_time", menuListVO.getStartTime(), menuListVO.getEndTime());
        queryWrapper.orderByDesc("create_time");
        return menuListDao.selectPage(menuListPage, queryWrapper);
    }

    @Override
    public List<MenuList> selectList(MenuListVO menuListVO) {
        QueryWrapper<MenuList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(null != menuListVO.getRoleId(), "role_id", menuListVO.getRoleId());
        queryWrapper.eq(null != menuListVO.getParentId(), "parent_id", menuListVO.getParentId());
        queryWrapper.like(StringUtils.isNotBlank(menuListVO.getName()), "name", menuListVO.getName());
        queryWrapper.eq(null != menuListVO.getState(), "state", menuListVO.getState());
        queryWrapper.between(null != menuListVO.getStartTime() && null != menuListVO.getEndTime(), "create_time", menuListVO.getStartTime(), menuListVO.getEndTime());
        queryWrapper.orderByDesc("create_time");
        return menuListDao.selectList(queryWrapper);
    }

    @Override
    public List<MenuList> menuList(MenuListVO menuListVO) {
        QueryWrapper<MenuList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", menuListVO.getRoleId());
        List<MenuList> menuLists = menuListDao.selectList(queryWrapper);
        return menuLists;
    }
}