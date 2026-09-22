package com.example.enterprise.common.core.constant;

/**
 * Unified business / HTTP-aligned error codes.
 * <p>
 * Convention:
 * <ul>
 *   <li>200 – success</li>
 *   <li>400 – bad request / validation</li>
 *   <li>401 – unauthenticated</li>
 *   <li>403 – forbidden</li>
 *   <li>404 – not found</li>
 *   <li>409 – conflict</li>
 *   <li>429 – too many requests</li>
 *   <li>500 – internal error</li>
 *   <li>1xxx – auth domain</li>
 *   <li>2xxx – user domain</li>
 *   <li>3xxx – system domain</li>
 * </ul>
 */
public enum ErrorCode {

    SUCCESS(200, "success"),

    BAD_REQUEST(400, "参数错误"),
    VALIDATION_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未认证或登录已失效"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "数据冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_ERROR(500, "系统异常"),

    // Auth domain 1xxx
    LOGIN_FAILED(1001, "用户名或密码错误"),
    CAPTCHA_INVALID(1002, "验证码错误或已过期"),
    ACCOUNT_DISABLED(1003, "账号已禁用"),
    TOKEN_INVALID(1004, "Token 无效或已过期"),
    LOGIN_LOCKED(1005, "登录失败次数过多，请稍后再试"),

    // User domain 2xxx
    USER_NOT_FOUND(2001, "用户不存在"),
    USERNAME_EXISTS(2002, "用户名已存在"),
    PASSWORD_SAME(2003, "新密码不能与旧密码相同"),
    OLD_PASSWORD_WRONG(2004, "原密码错误");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
