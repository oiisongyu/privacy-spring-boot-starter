package cn.zhz.privacy.interceptor;

import cn.zhz.privacy.PrivacyApplication;
import cn.zhz.privacy.crypto.ICrypto;
import cn.zhz.privacy.enums.Algorithm;
import cn.zhz.privacy.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author z.h.z
 * @since 2023/10/12
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PrivacyApplication.class})
public class CryptoInterceptorTest {

    @Autowired
    private IInnerInterceptor cryptoInterceptor;
    @Autowired
    private ICrypto defaultCrypto;

    /**
     * 查询加密参数
     */
    @Test
    public void selectTest1() {
        Object param = getParam();
        cryptoInterceptor.beforeQuery(null, null, param, null, null, null);
        Assert.assertEquals(getAssertParam(), param);
    }

    /**
     * 查询解密结果
     */
    @Test
    public void selectTest2() {
        List<Object> resultList = getResultList();

        cryptoInterceptor.afterQuery(resultList);

        Assert.assertEquals(getAssertResulList(), resultList);
    }



    private List<Object> getResultList() {
        List<Object> paramList = new ArrayList<>();
        try {
            Collections.addAll(paramList,
                    new UserDto("zhangsan", defaultCrypto.encrypt(Algorithm.AES, "123456", null), "19969999999", 1),
                    new UserDto("lisi", defaultCrypto.encrypt(Algorithm.AES, "654321", null), "16969999666", 1)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return paramList;
    }

    private List<Object> getAssertResulList() {
        List<Object> paramList = new ArrayList<>();
        Collections.addAll(paramList,
                new UserDto("zhangsan", "123456", "19969999999", 1),
                new UserDto("lisi", "654321", "16969999666", 1)
        );
        return paramList;
    }

    private Object getParam() {
        return new UserDto("zhangsan", "123456", "19969999999", 1);
    }

    private Object getAssertParam() {
        try {
            return new UserDto("zhangsan", defaultCrypto.encrypt(Algorithm.AES, "123456", null), "19969999999", 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
