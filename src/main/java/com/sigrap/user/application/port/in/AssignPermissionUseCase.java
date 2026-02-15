package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.Role;

/**
 * Input port for assigning a permission to a role.
 * This interface defines the use case for permission assignment.
 */
public interface AssignPermissionUseCase {
    
    /**
     * Assigns a permission to a role.
     *
     * @param roleId the identifier of the role
     * @param permissionId the identifier of the permission to assign
     * @return the updated role domain entity
     * @throws IllegalArgumentException if the role or permission is not found
     */
    Role assignPermission(Long roleId, Long permissionId);
}
