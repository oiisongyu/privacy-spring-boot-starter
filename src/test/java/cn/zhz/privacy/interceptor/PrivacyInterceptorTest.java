package cn.zhz.privacy.interceptor;

import cn.zhz.privacy.PrivacyApplication;
import cn.zhz.privacy.crypto.ICrypto;
import cn.zhz.privacy.enums.Algorithm;
import cn.zhz.privacy.model.User1Dto;
import cn.zhz.privacy.model.User2Dto;
import cn.zhz.privacy.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author z.h.z
 * @since 2023/10/12
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PrivacyApplication.class})
public class PrivacyInterceptorTest {

    @Autowired
    private List<IInnerInterceptor> iInnerInterceptorList = new ArrayList<>();

    @Autowired
    private ICrypto defaultCrypto;


    /**
     * 查询前置
     */
    @Test
    public void selectTest1() {
        Map<String, Object> map = new HashMap<>();
        List<Object> paramList = getParamList();
        map.put("list", paramList);
        iInnerInterceptorList.forEach(iInnerInterceptor -> iInnerInterceptor.beforeQuery(null, null, map, null, null, null));
        Assert.assertEquals(getAssertParamList(), map.get("list"));
    }

    /**
     * 查询前置
     */
    @Test
    public void selectTest1_1() {
        Map<String, Object> map = new HashMap<>();
        List<Object> paramList = getParamList();
        map.put("list", paramList);
        iInnerInterceptorList.forEach(iInnerInterceptor -> iInnerInterceptor.beforeQuery(null, null, map, null, null, null));
        Assert.assertEquals(getAssertParamList(), map.get("list"));
    }

    /**
     * 查询后置
     */
    @Test
    public void selectTest2() {
        List<Object> resultList = getResultList();
        iInnerInterceptorList.forEach(iInnerInterceptor -> iInnerInterceptor.afterQuery(resultList));
        Assert.assertEquals(getAssertResulList(), resultList);
    }

    /**
     * 修改前置
     */
    @Test
    public void selectTest3() {
        Map<String, Object> map = new HashMap<>();
        List<Object> paramList = getParamList();
        map.put("list", paramList);
        map.put("list2", paramList);
        iInnerInterceptorList.forEach(iInnerInterceptor -> iInnerInterceptor.beforeUpdate(map));
        Assert.assertEquals(getAssertParamList(), map.get("list"));
    }

    /**
     * 查询后置-嵌套类型
     */
    @Test
    public void selectTest4() {
        List<Object> resultList = getResultList4();
        iInnerInterceptorList.forEach(iInnerInterceptor -> iInnerInterceptor.afterQuery(resultList));
        Assert.assertEquals(getAssertResulList4(), resultList);
    }
    /**
     * 查询后置-嵌套类型
     */
    @Test
    public void selectTest5() {
        List<Object> resultList = getResultList5();
        iInnerInterceptorList.forEach(iInnerInterceptor -> iInnerInterceptor.afterQuery(resultList));
//        Assert.assertEquals(getAssertResulList4(), resultList);
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

    private List<Object> getResultList4() {
        List<Object> paramList = new ArrayList<>();
        try {
            Collections.addAll(paramList,
                    new User1Dto("zhangsan", defaultCrypto.encrypt(Algorithm.AES, "123456", null), "19969999999", 1, new User1Dto("zhangsan2", defaultCrypto.encrypt(Algorithm.AES, "123456", null), "19969999999", 1, null)),
                    new User1Dto("lisi", defaultCrypto.encrypt(Algorithm.AES, "654321", null), "16969999666", 1, null)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return paramList;
    }

    private List<Object> getResultList5() {
        List<Object> paramList = new ArrayList<>();
        List<User2Dto> paramList1 = new ArrayList<>();
        try {
            Collections.addAll(paramList,
                    new User2Dto("zhangsan", defaultCrypto.encrypt(Algorithm.AES, "123456", null), "19969999999", 1, null),
                    new User2Dto("lisi", defaultCrypto.encrypt(Algorithm.AES, "654321", null), "16969999666", 1, null)
            );

                     Collections.addAll(paramList1,
                    new User2Dto("zhangsan", defaultCrypto.encrypt(Algorithm.AES, "123456", null), "19969999999", 1, null),
                    new User2Dto("lisi", defaultCrypto.encrypt(Algorithm.AES, "654321", null), "16969999666", 1, null)
            );



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<Object> paramList2 = new ArrayList<>();
        try {
            Collections.addAll(paramList2,
                    new User2Dto("zhangsan", defaultCrypto.encrypt(Algorithm.AES, "123456", null), "19969999999", 1, paramList1),
                    new User2Dto("lisi", defaultCrypto.encrypt(Algorithm.AES, "654321", null), "16969999666", 1, null)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return paramList2;
    }

    private List<Object> getAssertResulList() {
        List<Object> paramList = new ArrayList<>();
        Collections.addAll(paramList,
                new UserDto("zhangsan", "123456", "199****9999", 1),
                new UserDto("lisi", "654321", "169****9666", 1)
        );
        return paramList;
    }

    private List<Object> getAssertResulList4() {
        List<Object> paramList = new ArrayList<>();
        Collections.addAll(paramList,
                new User1Dto("zhangsan", "123456", "199****9999", 1, new User1Dto("zhangsan2", "123456", "199****9999", 1, null)),
                new User1Dto("lisi", "654321", "169****9666", 1, null)
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
