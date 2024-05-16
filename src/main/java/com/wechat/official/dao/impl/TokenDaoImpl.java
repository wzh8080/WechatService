package com.wechat.official.dao.impl;

import com.wechat.official.dao.TokenDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class TokenDaoImpl implements TokenDao {
    @Value("${wechat.official.token.expire.minute}")
    private int expireMin;

    private final JdbcTemplate jdbcTemplate;

    public TokenDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 若token过期，尝试从数据库获取新的token并更新缓存。
     * @return 不过期的token字符串，如果不存在或过期则返回null。
     */
    @Override
    @Nullable
    public String getToken() {
        String sql = "SELECT token FROM official_token WHERE expire_time > NOW()";
        List<String> tokenList = jdbcTemplate.queryForList(sql, String.class);
        if (!tokenList.isEmpty()) {
            return tokenList.get(0); // 假设你仍然需要返回一个String，这里简单取第一个元素
        } else {
            return null;
        }
    }

    @Override
    public void setToken(String token) {
        jdbcTemplate.update("truncate table official_token");
        jdbcTemplate.update("insert into official_token (token,expire_config) values (?,?)", token,expireMin);
    }


}
