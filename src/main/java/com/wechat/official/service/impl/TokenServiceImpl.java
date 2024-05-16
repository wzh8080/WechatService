package com.wechat.official.service.impl;

import com.util.http.HttpUtils;
import com.util.http.WeChatTokenResponse;
import com.wechat.official.dao.TokenDao;
import com.wechat.official.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 * 1. 使用 redis 缓存，直接调用 api 放到 redis 中
 * 2. 定时任务，定时刷新 token
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    // 使用配置项来代替硬编码的值
    private static final ConcurrentHashMap<String, String> tokenCache = new ConcurrentHashMap<>();

    @Value("${wechat.official.token.id}")
    private String appId;
    @Value("${wechat.official.token.secret}")
    private String appSecret;
    @Value("${wechat.official.token.url}")
    private String tokenUrl;
    @Value("${wechat.official.token.key}")
    private String tokenKey;

    private final TokenDao tokenDao;

    public TokenServiceImpl(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    @Override
    @Nullable
    public String getToken() {
        // 会阻塞 tokenCache.put(tokenKey, accessToken);
//        return tokenCache.computeIfAbsent(tokenKey, this::getTokenFromDatabaseOrAPI);
        try {
            if (tokenCache.get(tokenKey) != null) {
                return tokenCache.get(tokenKey);
            }else{
                return getTokenFromDatabaseOrAPI(tokenKey);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }
    private String getTokenFromDatabaseOrAPI(String key) {
        return refreshTokenFromDatabase(key);
    }

    /**
     * 从数据库中获取token
     * @param key
     * @return
     */
    private String refreshTokenFromDatabase(String key) {
        String dbToken = tokenDao.getToken();
        if (dbToken != null) {
            log.debug(Thread.currentThread().getName() + "   获取数据库中的token：" + dbToken);
            tokenCache.put(key, dbToken);
            return dbToken;
        } else {
            log.debug("调用 API 获取 token, fetching from WeChat API...");
            String apiToken = null;
            try {
                apiToken = fetchTokenFromAPI();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
            tokenDao.setToken(apiToken);
            return apiToken;
        }
    }
    private String fetchTokenFromAPI() throws Exception {
        String url = String.format(tokenUrl, appId, appSecret);
        WeChatTokenResponse response = HttpUtils.sendGetRequest(url, WeChatTokenResponse.class);
        if (response != null) {
            if (response.getExpires_in() != 7200) {
                throw new Exception("获取微信 token 失败，网络异常！状态码：" + response.getExpires_in());
            }
            String accessToken = response.getAccess_token();
            tokenCache.put(tokenKey, accessToken);
            log.debug(Thread.currentThread().getName() + "   token fetched from WeChat API: " + accessToken);
            return accessToken;
        } else {
            log.error("连接异常！调用 api 失败！");
            return null;
        }
    }
}
