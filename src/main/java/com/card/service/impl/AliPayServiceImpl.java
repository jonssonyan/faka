package com.card.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.card.dao.AliPayConfigDao;
import com.card.entity.domain.AliPayConfig;
import com.card.service.AliPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.alipay.api.AlipayConstants.CHARSET;

@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    private AliPayConfigDao aliPayConfigDao;

    @Override
    public AlipayTradePrecreateResponse faceToFace(String subject, String outTradeNo, String totalAmount) {
        try {
            AliPayConfig aliPayConfig = aliPayConfigDao.selectById(1);
            Config config = new Config();
            config.protocol = "https";
            config.gatewayHost = "openapi.alipay.com";
            config.signType = "RSA2";

            config.appId = aliPayConfig.getAppId();

            // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
            config.merchantPrivateKey = aliPayConfig.getMerchantPrivateKey();

            //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载
            // config.merchantCertPath = "<-- 请填写您的应用公钥证书文件路径，例如：/foo/appCertPublicKey_2019051064521003.crt -->";
            // config.alipayCertPath = "<-- 请填写您的支付宝公钥证书文件路径，例如：/foo/alipayCertPublicKey_RSA2.crt -->";
            // config.alipayRootCertPath = "<-- 请填写您的支付宝根证书文件路径，例如：/foo/alipayRootCert.crt -->";

            //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
            config.alipayPublicKey = aliPayConfig.getAliPayPublicKey();

            //可设置异步通知接收服务地址（可选）
            // config.notifyUrl = "<-- 请填写您的支付类接口异步通知接收服务地址，例如：https://www.test.com/callback -->";

            //可设置AES密钥，调用AES加解密相关接口时需要（可选）
            //config.encryptKey = "BNuD0FNyyPabIcASj2eb3Q==";

            // 1. 设置参数（全局只需设置一次）
            Factory.setOptions(config);
            // 2. 发起API调用（以创建当面付收款二维码为例）// 二维码有效期为2小时
            return Factory.Payment.FaceToFace().optional("","").preCreate(subject, outTradeNo, totalAmount);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("预支付失败！");
        }
    }

    @Override
    public String queryTrade(String outTradeNo) {
        try {
            AliPayConfig aliPayConfig = aliPayConfigDao.selectById(1);
            //获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliPayConfig.getAppId(), aliPayConfig.getMerchantPrivateKey(), "json", "GBK", aliPayConfig.getAliPayPublicKey(), "RSA2");
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest(); //创建API对应的request类
            request.setBizContent("{" + "\"out_trade_no\":\"" + outTradeNo + "\"}");  //设置业务参数
            //通过alipayClient调用API，获得对应的response类，在获取action属性，本次撤销触发的交易动作 close：关闭交易，无退款 refund：产生了退款
            return alipayClient.execute(request).getTradeStatus();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询交易失败！");
        }
    }

    @Override
    public String cancelTrade(String outTradeNo) {
        try {
            AliPayConfig aliPayConfig = aliPayConfigDao.selectById(1);
            //获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliPayConfig.getAppId(), aliPayConfig.getMerchantPrivateKey(), "json", "GBK", aliPayConfig.getAliPayPublicKey(), "RSA2");
            AlipayTradeCancelRequest request = new AlipayTradeCancelRequest(); //创建API对应的request类
            request.setBizContent("{" + "\"out_trade_no\":\"" + outTradeNo + "\"}");  //设置业务参数
            //通过alipayClient调用API，获得对应的response类，在获取action属性，本次撤销触发的交易动作 close：关闭交易，无退款 refund：产生了退款
            return alipayClient.execute(request).getAction();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("撤销交易失败！");
        }
    }

    @Override
    public void updateById(Long id, AliPayConfig aliPayConfig) {
        aliPayConfigDao.updateById(id, aliPayConfig);
    }

    @Override
    public AliPayConfig selectById(Long id) {
        return aliPayConfigDao.selectById(1);
    }
}