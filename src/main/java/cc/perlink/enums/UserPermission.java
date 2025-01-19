package cc.perlink.enums;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
public enum UserPermission {
    USER("user"),
    ADMIN("admin"),
    BANNED("banned");

    private String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
