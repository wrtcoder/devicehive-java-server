package com.devicehive.dao.riak.model;

import com.basho.riak.client.api.annotations.RiakIndex;
import com.devicehive.vo.OAuthClientVO;

public class RiakOAuthClient {

    private Long id;

    private String name;

    private String domain;

    private String subnet;

    private String redirectUri;

    private String oauthId;

    private String oauthSecret;

    private long entityVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getOauthSecret() {
        return oauthSecret;
    }

    public void setOauthSecret(String oauthSecret) {
        this.oauthSecret = oauthSecret;
    }

    //Riak indexes
    @RiakIndex(name = "name")
    public String getNameSi() {
        return name;
    }

    @RiakIndex(name = "oauthId")
    public String getOauthIdSi() {
        return oauthId;
    }

    public static OAuthClientVO convert(RiakOAuthClient client) {
        if (client != null) {
            OAuthClientVO vo = new OAuthClientVO();
            vo.setId(client.getId());
            vo.setName(client.getName());
            vo.setDomain(client.getDomain());
            vo.setSubnet(client.getSubnet());
            vo.setRedirectUri(client.getRedirectUri());
            vo.setOauthId(client.getOauthId());
            vo.setOauthSecret(client.getOauthSecret());
            return vo;
        } else {
            return null;
        }
    }
    public static RiakOAuthClient convert(OAuthClientVO client) {
        if (client != null) {
            RiakOAuthClient vo = new RiakOAuthClient();
            vo.setId(client.getId());
            vo.setName(client.getName());
            vo.setDomain(client.getDomain());
            vo.setSubnet(client.getSubnet());
            vo.setRedirectUri(client.getRedirectUri());
            vo.setOauthId(client.getOauthId());
            vo.setOauthSecret(client.getOauthSecret());
            return vo;
        } else {
            return null;
        }
    }
}
