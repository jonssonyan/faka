package com.card.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.card.dao.CardDao;
import com.card.entity.Card;
import com.card.entity.vo.CardVO;
import com.card.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CardService extends ServiceImpl<CardDao, Card> {
    @Autowired
    private CardDao cardDao;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    public IPage<Card> selectPage(CardVO cardVO) {
        List<Long> longs = userService.selectUserIds(SecurityUtil.getCurrentUser().getId(), true);
        IPage<Card> cardIPage = lambdaQuery().eq(null != cardVO.getProductId(), Card::getProductId, cardVO.getProductId())
                .like(StringUtils.isNotBlank(cardVO.getContent()), Card::getContent, cardVO.getContent())
                .eq(null != cardVO.getState(), Card::getState, cardVO.getState())
                .between(null != cardVO.getStartTime() && null != cardVO.getEndTime(), Card::getCreateTime, cardVO.getStartTime(), cardVO.getEndTime())
                .in(Card::getCreator, longs)
                .orderByDesc(Card::getCreateTime)
                .page(new Page<>(cardVO.getPageNum(), cardVO.getPageSize()));
        cardIPage.getRecords().forEach(
                card -> card.setProduct(productService.getById(card.getProductId()))
        );
        return cardIPage;
    }

    public Integer countByProductId(Long productId) {
        return lambdaQuery().eq(Card::getProductId, productId)
                .count();
    }
}