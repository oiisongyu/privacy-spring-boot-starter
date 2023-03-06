package cn.zhz.privacy.config;

import cn.zhz.privacy.crypto.DefaultCrypto;
import cn.zhz.privacy.interceptor.CryptoInterceptor;
import cn.zhz.privacy.interceptor.DesensitizeInterceptor;
import cn.zhz.privacy.properties.CryptoProperties;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

public class PrivacyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        List<String> className = new ArrayList<>();
        className.add(CryptoInterceptor.class.getName());
        className.add(DesensitizeInterceptor.class.getName());
        className.add(CryptoProperties.class.getName());
        className.add(DefaultCrypto.class.getName());
        return className.toArray(new String[className.size()]);
    }
}

