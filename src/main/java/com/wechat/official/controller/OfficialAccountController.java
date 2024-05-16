package com.wechat.official.controller;

import cn.hutool.crypto.SecureUtil;
import com.wechat.official.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("official")
@Slf4j
public class OfficialAccountController {
    private final TokenService tokenService;

    public OfficialAccountController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("validate")
    public String validate(String signature,String timestamp,String nonce,String echostr){
        // 1. 将token、timestamp、nonce三个参数进行字典序排序
        String[] arr = {timestamp, nonce, tokenService.getToken()};
        Arrays.sort(arr);
        // 2. 将三个参数字符串拼接成一个字符串进行sha1加密
        StringBuilder sb = new StringBuilder();
        for (String temp : arr) {
            sb.append(temp);
        }
        // 这里利用了 hutool 的加密工具类
        String sha1 = SecureUtil.sha1(sb.toString());
        // 3. 加密后的字符串与signature对比，如果相同则该请求来源于微信，原样返回echostr
        if (sha1.equals(signature)){
            log.info("~~~~~~~~~~~~~~~~ 接入成功！~~~~~~~~~~~~~~~~");
            return echostr;
        }
        // 接入失败
        return null;
    }
}

