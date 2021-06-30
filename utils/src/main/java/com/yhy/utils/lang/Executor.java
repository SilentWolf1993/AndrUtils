package com.yhy.utils.lang;

/**
 * 处理器
 * <p>
 * Created on 2021-06-30 22:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface Executor<T, E> {

    /**
     * 处理
     *
     * @param resolver 正向处理器
     * @param rejector 异常处理器
     */
    void execute(Resolver<T> resolver, Rejector<E> rejector);
}
