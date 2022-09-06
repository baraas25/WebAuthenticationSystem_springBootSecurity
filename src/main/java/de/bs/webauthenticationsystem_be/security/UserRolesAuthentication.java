package de.bs.webauthenticationsystem_be.security;

import lombok.Getter;

@Getter
public enum UserRolesAuthentication {
    USER_PERMISSIONS("user:read"),
    MANAGER_PERMISSIONS("user:read","user:update"),
    ADMIN_PERMISSIONS("user:read","user:update","user:create"),
    SUPER_ADMIN_PERMISSIONS("user:read","user:update","user:create","user:delete");

    private String[] authentications;

    UserRolesAuthentication(String... authentications) {
        this.authentications = authentications;
    }
}
