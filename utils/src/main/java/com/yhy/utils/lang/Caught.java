package com.yhy.utils.lang;

/**
 * 异常回调
 * <p>
 * Created on 2021-06-30 22:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface Caught<T> {

    /**
     * 异常回调
     *
     * @param error 错误信息
     */
    void caught(T error);
}
