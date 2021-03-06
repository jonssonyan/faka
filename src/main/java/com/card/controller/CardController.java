package com.card.controller;

import com.card.entity.Card;
import com.card.entity.vo.CardVO;
import com.card.entity.vo.Result;
import com.card.security.utils.SecurityUtil;
import com.card.service.CardService;
import com.card.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    /**
     * 通过productId计算产品的库存
     *
     * @param cardVO
     * @return
     */
    @PostMapping("/countByProductId")
    public Result<Object> countByProductId(@RequestBody CardVO cardVO) {
        return Result.success(cardService.countByProductId(cardVO.getProductId()));
    }

    /**
     * 分页查询卡密内容
     *
     * @param cardVO
     * @return
     */
    @PostMapping("/selectPage")
    public Result<Object> selectPage(@RequestBody CardVO cardVO) {
        return Result.success(cardService.selectPage(cardVO));
    }

    /**
     * 添加或者更新卡密
     *
     * @param card 卡密对象
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public Result<Object> saveOrUpdate(@RequestBody Card card) {
        List<Long> longs = userService.selectUserIds(SecurityUtil.getCurrentUser().getId(), true);
        if (card.getId() == null) card.setCreator(SecurityUtil.getCurrentUser().getId());
        // TODO: 2021/1/20 更新之前需要校验这个卡密的创建人是不是当前用户的下级，怎样写才会让代码更加优雅？
        cardService.saveOrUpdate(card);
        return Result.success();
    }

    /**
     * 通过id查询卡密信息
     *
     * @param cardVO
     * @return
     */
    @PostMapping("/getById")
    public Result<Object> getById(@RequestBody CardVO cardVO) {
        List<Long> longs = userService.selectUserIds(SecurityUtil.getCurrentUser().getId(), true);
        Card card = cardService.lambdaQuery()
                .in(Card::getCreator, longs)
                .eq(Card::getId, cardVO.getId())
                .one();
        return Result.success(card);
    }

    /**
     * 删除卡密
     * 需要管理员权限
     *
     * @param cardVO
     * @return
     */
    @PostMapping("/removeById")
    public Result<Object> removeById(@RequestBody CardVO cardVO) {
        List<Long> longs = userService.selectUserIds(SecurityUtil.getCurrentUser().getId(), true);
        boolean remove = cardService.lambdaUpdate()
                .in(Card::getCreator, longs)
                .eq(Card::getId, cardVO.getId())
                .remove();
        if (remove) log.info("用户id{}删除卡密{}", SecurityUtil.getCurrentUser().getId(), cardVO.getContent());
        return Result.success();
    }
}