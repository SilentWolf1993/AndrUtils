package com.yhy.utils.lang;

/**
 * 正常逻辑回调
 * <p>
 * Created on 2021-06-30 22:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface Then<T> {

    /**
     * 正常处理
     *
     * @param data 回调数据
     */
    void then(T data);
}
