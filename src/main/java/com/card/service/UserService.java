package com.card.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.card.command.user.UserCommand;
import com.card.entity.domain.User;

public interface UserService {
    IPage<User> findByPage(Integer pageNum, Integer pageSize, UserCommand userCommand);

    void updateById(Long id, User user);

    void deleteById(Long id);

    void insert(User user);
}