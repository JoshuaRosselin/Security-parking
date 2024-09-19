package org.grupouno.parking.it4.service;

import java.util.List;

public interface IRoleService {

     List<String> findRolesByProfileId(Long profileId);
}
