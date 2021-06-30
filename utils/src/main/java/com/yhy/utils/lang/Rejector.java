package com.yhy.utils.lang;

/**
 * 触发异常
 * <p>
 * Created on 2021-06-30 22:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface Rejector<T> {

    /**
     * 触发异常
     *
     * @param error 异常信息
     */
    void reject(T error);
}
