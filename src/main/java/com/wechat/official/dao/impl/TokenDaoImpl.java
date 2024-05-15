package com.wechat.official.dao.impl;

import com.wechat.official.dao.TokenDao;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public class TokenDaoImpl implements TokenDao {
    /**
     * 若 token 过期，返回null ， 不过期则取库中 token
     * @return
     */
    @Override
    @Nullable
    public String getToken() {
        
        return null;
    }
}
