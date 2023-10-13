package cn.zhz.privacy.interceptor;


import cn.zhz.privacy.handler.DesensitizeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author ZHZ
 * @date 2021-11-15
 * @apiNote
 */
@RequiredArgsConstructor
@Slf4j
public class DesensitizeInterceptor implements IInnerInterceptor {


    private final DesensitizeHandler desensitizeHandler;

    @Override
    public void afterQuery(List<Object> resultList) {
        if (resultList != null && !resultList.isEmpty()) {
            log.debug("脱敏前查询结果：" + resultList);
            desensitizeHandler.handleResultList(resultList);
            log.debug("脱敏后查询结果：" + resultList);
        }
    }


}
