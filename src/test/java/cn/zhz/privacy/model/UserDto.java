package cn.zhz.privacy.model;

import cn.zhz.privacy.annotation.FieldDesensitize;
import cn.zhz.privacy.annotation.FieldEncrypt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author z.h.z
 * @since 2023/10/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {


    private String username;
    @FieldEncrypt
    private String password;

    @FieldDesensitize
    private String phone;

    private Integer sex;
}
