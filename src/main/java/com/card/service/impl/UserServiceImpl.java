package com.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.card.dao.UserDao;
import com.card.entity.User;
import com.card.entity.vo.UserVO;
import com.card.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public IPage<User> selectPage(UserVO userVO) {
        Page<User> userPage = new Page<>(userVO.getPageNum(), userVO.getPageSize());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(userVO.getUsername()), "username", userVO.getUsername());
        wrapper.like(StringUtils.isNotBlank(userVO.getEmail()), "email", userVO.getEmail());
        wrapper.like(StringUtils.isNotBlank(userVO.getQq()), "qq", userVO.getQq());
        wrapper.like(StringUtils.isNotBlank(userVO.getPhone()), "phone", userVO.getPhone());
        wrapper.like(StringUtils.isNotBlank(userVO.getEmail()), "email", userVO.getEmail());
        wrapper.eq(null != userVO.getState(), "state", userVO.getState());
        wrapper.eq(null != userVO.getRoleId(), "role_id", userVO.getRoleId());
        wrapper.eq(null != userVO.getParentId(), "parent_id", userVO.getParentId());
        wrapper.between(null != userVO.getStartTime() && null != userVO.getEndTime(), "create_time", userVO.getStartTime(), userVO.getEndTime());
        wrapper.orderByDesc("create_time");
        return userDao.selectPage(userPage, wrapper);
    }

    @Override
    public void insert(User user) {
        userDao.insert(user);
    }

    @Override
    public User selectByUsernameAndPassword(UserVO userVO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, userVO.getUsername()).eq(User::getPassword, userVO.getPassword());
        return userDao.selectOne(queryWrapper);
    }

    @Override
    public Integer countByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userDao.selectCount(queryWrapper);
    }

    @Override
    public User selectById(Long id) {
        return userDao.selectById(id);
    }

    @Override
    public void deleteBatchIds(List<Long> ids) {
        userDao.deleteBatchIds(ids);
    }

    @Override
    public List<Long> selectIdsByParentId(Long parentId) {
        return selectByParentId(parentId);
    }

    /**
     * 查询用户及下级用户的id集合
     *
     * @param parentId
     * @return
     */
    List<Long> selectByParentId(Long parentId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        List<User> users = userDao.selectList(queryWrapper);
        List<Long> collect = users.stream().map(User::getId).collect(Collectors.toList());
        ArrayList<Long> list = new ArrayList<>(collect);
        list.add(parentId);
        for (Long aLong : collect) {
            list.addAll(selectByParentId(aLong));
        }
        return list;
    }
}