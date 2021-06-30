package com.yhy.utils.lang;

/**
 * 正常处理
 * <p>
 * Created on 2021-06-30 22:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface Resolver<T> {

    /**
     * 正常处理
     *
     * @param data 回调数据
     */
    void resolve(T data);
}
