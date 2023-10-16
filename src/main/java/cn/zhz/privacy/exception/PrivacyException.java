/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.zhz.privacy.exception;

/**
 * Privacy 异常类
 *
 * @author zhz
 * @since 2023-10-16
 */
public class PrivacyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PrivacyException(String message) {
        super(message);
    }

    public PrivacyException(Throwable throwable) {
        super(throwable);
    }

    public PrivacyException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * 如果flag==true，则抛出message异常
     *
     * @param flag    标记
     * @param message 异常信息
     */
    public static void throwBy(boolean flag, String message) {
        if (flag) {
            throw new PrivacyException(message);
        }
    }
}
