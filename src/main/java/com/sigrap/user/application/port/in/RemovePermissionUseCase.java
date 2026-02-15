package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.Role;

/**
 * Input port for removing a permission from a role.
 * This interface defines the use case for permission removal.
 */
public interface RemovePermissionUseCase {
    
    /**
     * Removes a permission from a role.
     *
     * @param roleId the identifier of the role
     * @param permissionId the identifier of the permission to remove
     * @return the updated role domain entity
     * @throws IllegalArgumentException if the role or permission is not found
     */
    Role removePermission(Long roleId, Long permissionId);
}
