package com.wechat.official.dao.impl;

import com.wechat.official.dao.TokenDao;
import com.wechat.official.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class OfficialTokenTest {
    @Autowired
    private TokenDao tokenDao;
    @Autowired
    private TokenService tokenService;
    @Test
    void getToken() {
        System.out.println("输出："+tokenService.getToken());
        System.out.println("----------------------");
        System.out.println("输出："+tokenService.getToken());
    }
}