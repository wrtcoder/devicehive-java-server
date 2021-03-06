package com.devicehive.dao;

import com.devicehive.auth.HivePrincipal;
import com.devicehive.vo.DeviceVO;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by Gleb on 07.07.2016.
 */
public interface DeviceDao {

    DeviceVO findByUUID(String uuid);

    void persist(DeviceVO device);

    DeviceVO merge(DeviceVO device);

    int deleteByUUID(String guid);

    List<DeviceVO> getDeviceList(List<String> guids, HivePrincipal principal);

    long getAllowedDeviceCount(HivePrincipal principal, List<String> guids);

    List<DeviceVO> getList(String name, String namePattern, String status, Long networkId, String networkName,
                         Long deviceClassId, String deviceClassName, String sortField, @NotNull Boolean sortOrderAsc, Integer take,
                         Integer skip, HivePrincipal principal);

    Map<String, Integer> getOfflineTimeForDevices(List<String> guids);

    void changeStatusForDevices(String status, List<String> guids);

}
