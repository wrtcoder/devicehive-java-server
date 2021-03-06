package com.devicehive.dao.riak.model;

import com.basho.riak.client.api.annotations.RiakIndex;
import com.devicehive.vo.NetworkVO;
import com.devicehive.vo.NetworkWithUsersAndDevicesVO;

public class RiakNetwork {

    private Long id;
    private String key;
    private String name;
    private String description;
    private Long entityVersion;

    public RiakNetwork() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }

    @RiakIndex(name = "name")
    public String getNameSi() {
        return getName();
    }

    public static NetworkVO convert(RiakNetwork network) {
        NetworkVO vo = null;
        if (network != null) {
            vo = new NetworkVO();
            vo.setId(network.getId());
            vo.setKey(network.getKey());
            vo.setName(network.getName());
            vo.setDescription(network.getDescription());
            vo.setEntityVersion(network.getEntityVersion());
        }
        return vo;
    }

    public static RiakNetwork convert(NetworkVO network) {
        RiakNetwork vo = null;
        if (network != null) {
            vo = new RiakNetwork();
            vo.setId(network.getId());
            vo.setKey(network.getKey());
            vo.setName(network.getName());
            vo.setDescription(network.getDescription());
            vo.setEntityVersion(network.getEntityVersion());
        }
        return vo;
    }
}
