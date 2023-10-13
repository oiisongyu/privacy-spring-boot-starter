package cn.zhz.privacy.interceptor;

import cn.zhz.privacy.PrivacyApplication;
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
public class DesensitizeInterceptorTest {

    @Autowired
    private IInnerInterceptor desensitizeInterceptor;

    /**
     * 查询脱敏参数
     */
    @Test
    public void selectTest2() {
        List<Object> paramList = getParamList();
        desensitizeInterceptor.afterQuery(paramList);
        Assert.assertEquals(getAssertParamList(), paramList);
    }

    private List<Object> getParamList() {
        List<Object> paramList = new ArrayList<>();
        Collections.addAll(paramList,
                new UserDto("zhangsan", "123456", "19969999999", 1),
                new UserDto("lisi", "654321", "16969999666", 1)
        );
        return paramList;
    }

    private List<Object> getAssertParamList() {
        List<Object> paramList = new ArrayList<>();
        Collections.addAll(paramList,
                new UserDto("zhangsan", "123456", "199****9999", 1),
                new UserDto("lisi", "654321", "169****9666", 1)
        );
        return paramList;
    }

}
