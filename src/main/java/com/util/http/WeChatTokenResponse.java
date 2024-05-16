package com.util.http;

public class WeChatTokenResponse {
    private String access_token;
    private int expires_in; // 此字段在此上下文中不再使用，但保留以保持结构完整性

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}
