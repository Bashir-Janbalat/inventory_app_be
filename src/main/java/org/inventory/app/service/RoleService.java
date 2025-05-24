package org.inventory.app.service;

import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.RoleDTO;

import java.util.List;

public interface RoleService {

    ValueWrapper<List<RoleDTO>> getAllRoles();

    ValueWrapper<RoleDTO> createRole(String roleName);

}
