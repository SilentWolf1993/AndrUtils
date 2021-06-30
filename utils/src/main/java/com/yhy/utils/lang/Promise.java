package com.yhy.utils.lang;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * Promise 链式回调
 * <p>
 * Created on 2021-06-30 22:58
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class Promise<T, E> {
    private final Handler mHandler;
    private final List<Then<T>> mThenList;
    private final List<Caught<E>> mCaughtList;
    private Status status = Status.PADDING;
    private T data;
    private E error;

    public Promise(Executor<T, E> executor) {
        mHandler = new Handler(Looper.getMainLooper());
        mThenList = new ArrayList<>();
        mCaughtList = new ArrayList<>();

        Resolver<T> resolver = argData -> {
            if (status == Status.PADDING) {
                status = Status.RESOLVED;
            }

            data = argData;
            mThenList.forEach(item -> {
                mHandler.post(() -> {
                    item.then(data);
                });
            });
        };

        Rejector<E> rejector = argError -> {
            if (status == Status.PADDING) {
                status = Status.REJECTED;
            }

            error = argError;
            mCaughtList.forEach(item -> {
                mHandler.post(() -> {
                    item.caught(error);
                });
            });
        };

        try {
            executor.execute(resolver, rejector);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 正确的链式队列回调
     *
     * @param then 队列回调
     * @return 当前Promise对象
     */
    public Promise<T, E> then(Then<T> then) {
        if (status == Status.PADDING) {
            mThenList.add(then);
        }
        if (status == Status.RESOLVED) {
            mThenList.forEach(item -> {
                mHandler.post(() -> {
                    item.then(data);
                });
            });
        }
        return this;
    }

    /**
     * 错误消息的队列回调
     *
     * @param caught 队列回调
     * @return 当前Promise对象
     */
    public Promise<T, E> caught(Caught<E> caught) {
        if (status == Status.PADDING) {
            mCaughtList.add(caught);
        }
        if (status == Status.REJECTED) {
            mCaughtList.forEach(item -> {
                mHandler.post(() -> {
                    item.caught(error);
                });
            });
        }
        return this;
    }

    enum Status {
        PADDING,
        RESOLVED,
        REJECTED;
    }
}
