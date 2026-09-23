package com.example.enterprise.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "验证码")
public class CaptchaVO {

    @Schema(description = "验证码 ID，登录时回传")
    private String captchaId;

    @Schema(description = "验证码图片 Base64（data:image/png;base64,...）")
    private String imageBase64;

    /** 开发环境可返回明文便于联调；生产应关闭 */
    @Schema(description = "开发环境验证码明文，生产环境为 null")
    private String captchaCode;

    public CaptchaVO() {
    }

    public CaptchaVO(String captchaId, String imageBase64, String captchaCode) {
        this.captchaId = captchaId;
        this.imageBase64 = imageBase64;
        this.captchaCode = captchaCode;
    }

    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }
}

