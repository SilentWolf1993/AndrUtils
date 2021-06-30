package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-12-11 10:33
 * version: 1.0.0
 * desc   : 缓存工具类
 */
public class CacheUtils {
    private static final long DEFAULT_MAX_SIZE = Long.MAX_VALUE;
    private static final int DEFAULT_MAX_COUNT = Integer.MAX_VALUE;

    public static final int SEC = 1;
    public static final int MIN = 60;
    public static final int HOUR = 3600;
    public static final int DAY = 86400;

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private static final Map<String, CacheUtils> sCacheMap = new HashMap<>();
    private final CacheManager mCacheManager;

    /**
     * 初始化，在Application中
     *
     * @param ctx 上下文对象
     */
    public static void init(Context ctx) {
        CacheUtils.ctx = ctx;
    }

    /**
     * 获取缓存实例
     * <p>在/data/data/com.xxx.xxx/cache/cacheUtils目录</p>
     * <p>缓存尺寸不限</p>
     * <p>缓存个数不限</p>
     *
     * @return {@link CacheUtils}
     */
    public static CacheUtils getInstance() {
        return getInstance("", DEFAULT_MAX_SIZE, DEFAULT_MAX_COUNT);
    }

    /**
     * 获取缓存实例
     * <p>在/data/data/com.xxx.xxx/cache/cacheName目录</p>
     * <p>缓存尺寸不限</p>
     * <p>缓存个数不限</p>
     *
     * @param cacheName 缓存目录名
     * @return {@link CacheUtils}
     */
    public static CacheUtils getInstance(String cacheName) {
        return getInstance(cacheName, DEFAULT_MAX_SIZE, DEFAULT_MAX_COUNT);
    }

    /**
     * 获取缓存实例
     * <p>在/data/data/com.xxx.xxx/cache/cacheUtils目录</p>
     *
     * @param maxSize  最大缓存尺寸，单位字节
     * @param maxCount 最大缓存个数
     * @return {@link CacheUtils}
     */
    public static CacheUtils getInstance(long maxSize, int maxCount) {
        return getInstance("", maxSize, maxCount);
    }

    /**
     * 获取缓存实例
     * <p>在/data/data/com.xxx.xxx/cache/cacheName目录</p>
     *
     * @param cacheName 缓存目录名
     * @param maxSize   最大缓存尺寸，单位字节
     * @param maxCount  最大缓存个数
     * @return {@link CacheUtils}
     */
    public static CacheUtils getInstance(String cacheName, long maxSize, int maxCount) {
        if (isSpace(cacheName)) cacheName = "cacheUtils";
        File file = new File(ctx.getCacheDir(), cacheName);
        return getInstance(file, maxSize, maxCount);
    }

    /**
     * 获取缓存实例
     * <p>在cacheDir目录</p>
     * <p>缓存尺寸不限</p>
     * <p>缓存个数不限</p>
     *
     * @param cacheDir 缓存目录
     * @return {@link CacheUtils}
     */
    public static CacheUtils getInstance(@NonNull File cacheDir) {
        return getInstance(cacheDir, DEFAULT_MAX_SIZE, DEFAULT_MAX_COUNT);
    }

    /**
     * 获取缓存实例
     * <p>在cacheDir目录</p>
     *
     * @param cacheDir 缓存目录
     * @param maxSize  最大缓存尺寸，单位字节
     * @param maxCount 最大缓存个数
     * @return {@link CacheUtils}
     */
    public static CacheUtils getInstance(@NonNull File cacheDir, long maxSize, int maxCount) {
        final String cacheKey = cacheDir.getAbsoluteFile() + "_" + SysUtils.getProcessId();
        CacheUtils cache = sCacheMap.get(cacheKey);
        if (cache == null) {
            cache = new CacheUtils(cacheDir, maxSize, maxCount);
            sCacheMap.put(cacheKey, cache);
        }
        return cache;
    }

    private CacheUtils(@NonNull File cacheDir, long maxSize, int maxCount) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
        }
        mCacheManager = new CacheManager(cacheDir, maxSize, maxCount);
    }

    /**
     * 缓存中写入字节数组
     *
     * @param key   键
     * @param value 值
     */
    public void put(@NonNull String key, @NonNull byte[] value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入字节数组
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    public void put(@NonNull String key, @NonNull byte[] value, int saveTime) {
        if (value.length <= 0) return;
        if (saveTime >= 0) value = CacheHelper.newByteArrayWithTime(saveTime, value);
        File file = mCacheManager.getFileBeforePut(key);
        CacheHelper.writeFileFromBytes(file, value);
        mCacheManager.updateModify(file);
        mCacheManager.put(file);

    }

    /**
     * 缓存中读取字节数组
     *
     * @param key 键
     * @return 存在且没过期返回对应值，否则返回{@code null}
     */
    public byte[] getBytes(@NonNull String key) {
        return getBytes(key, null);
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在且没过期返回对应值，否则返回默认值{@code defaultValue}
     */
    public byte[] getBytes(@NonNull String key, byte[] defaultValue) {
        final File file = mCacheManager.getFileIfExists(key);
        if (file == null) return defaultValue;
        byte[] data = CacheHelper.readFile2Bytes(file);
        if (CacheHelper.isDue(data)) {
            mCacheManager.removeByKey(key);
            return defaultValue;
        }
        mCacheManager.updateModify(file);
        return CacheHelper.getDataWithoutDueTime(data);
    }

    /**
     * 缓存中写入String
     *
     * @param key   键
     * @param value 值
     */
    public void put(@NonNull String key, @NonNull String value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入String
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    public void put(@NonNull String key, @NonNull String value, int saveTime) {
        put(key, CacheHelper.string2Bytes(value), saveTime);
    }

    /**
     * 缓存中读取String
     *
     * @param key 键
     * @return 存在且没过期返回对应值，否则返回{@code null}
     */
    public String getString(@NonNull String key) {
        return getString(key, null);
    }

    /**
     * 缓存中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在且没过期返回对应值，否则返回默认值{@code defaultValue}
     */
    public String getString(@NonNull String key, String defaultValue) {
        byte[] bytes = getBytes(key);
        if (bytes == null) return defaultValue;
        return CacheHelper.bytes2String(bytes);
    }

    /**
     * 缓存中写入JSONObject
     *
     * @param key   键
     * @param value 值
     */
    public void put(@NonNull String key, @NonNull JSONObject value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入JSONObject
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    public void put(@NonNull String key, @NonNull JSONObject value, int saveTime) {
        put(key, CacheHelper.jsonObject2Bytes(value), saveTime);
    }

    /**
     * 缓存中读取JSONObject
     *
     * @param key 键
     * @return 存在且没过期返回对应值，否则返回{@code null}
     */
    public JSONObject getJSONObject(@NonNull String key) {
        return getJSONObject(key, null);
    }

    /**
     * 缓存中读取JSONObject
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在且没过期返回对应值，否则返回默认值{@code defaultValue}
     */
    public JSONObject getJSONObject(@NonNull String key, JSONObject defaultValue) {
        byte[] bytes = getBytes(key);
        if (bytes == null) return defaultValue;
        return CacheHelper.bytes2JSONObject(bytes);
    }

    /**
     * 缓存中写入JSONArray
     *
     * @param key   键
     * @param value 值
     */
    public void put(@NonNull String key, @NonNull JSONArray value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入JSONArray
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    public void put(@NonNull String key, @NonNull JSONArray value, int saveTime) {
        put(key, CacheHelper.jsonArray2Bytes(value), saveTime);
    }

    /**
     * 缓存中读取JSONArray
     *
     * @param key 键
     * @return 存在且没过期返回对应值，否则返回{@code null}
     */
    public JSONArray getJSONArray(@NonNull String key) {
        return getJSONArray(key, null);
    }

    /**
     * 缓存中读取JSONArray
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在且没过期返回对应值，否则返回默认值{@code defaultValue}
     */
    public JSONArray getJSONArray(@NonNull String key, JSONArray defaultValue) {
        byte[] bytes = getBytes(key);
        if (bytes == null) return defaultValue;
        return CacheHelper.bytes2JSONArray(bytes);
    }

    /**
     * 缓存中写入Bitmap
     *
     * @param key   键
     * @param value 值
     */
    public void put(@NonNull String key, @NonNull Bitmap value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入Bitmap
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    public void put(@NonNull String key, @NonNull Bitmap value, int saveTime) {
        put(key, CacheHelper.bitmap2Bytes(value), saveTime);
    }

    /**
     * 缓存中读取Bitmap
     *
     * @param key 键
     * @return 存在且没过期返回对应值，否则返回{@code null}
     */
    public Bitmap getBitmap(@NonNull String key) {
        return getBitmap(key, null);
    }

    /**
     * 缓存中读取Bitmap
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在且没过期返回对应值，否则返回默认值{@code defaultValue}
     */
    public Bitmap getBitmap(@NonNull String key, Bitmap defaultValue) {
        byte[] bytes = getBytes(key);
        if (bytes == null) return defaultValue;
        return CacheHelper.bytes2Bitmap(bytes);
    }

    /**
     * 缓存中写入Drawable
     *
     * @param key   键
     * @param value 值
     */
    public void put(@NonNull String key, @NonNull Drawable value) {
        put(key, CacheHelper.drawable2Bytes(value));
    }

    /**
     * 缓存中写入Drawable
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    public void put(@NonNull String key, @NonNull Drawable value, int saveTime) {
        put(key, CacheHelper.drawable2Bytes(value), saveTime);
    }

    /**
     * 缓存中读取Drawable
     *
     * @param key 键
     * @return 存在且没过期返回对应值，否则返回{@code null}
     */
    public Drawable getDrawable(@NonNull String key) {
        return getDrawable(key, null);
    }

    /**
     * 缓存中读取Drawable
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在且没过期返回对应值，否则返回默认值{@code defaultValue}
     */
    public Drawable getDrawable(@NonNull String key, Drawable defaultValue) {
        byte[] bytes = getBytes(key);
        if (bytes == null) return defaultValue;
        return CacheHelper.bytes2Drawable(bytes);
    }

    /**
     * 缓存中写入Parcelable
     *
     * @param key   键
     * @param value 值
     */
    public void put(@NonNull String key, @NonNull Parcelable value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入Parcelable
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    public void put(@NonNull String key, @NonNull Parcelable value, int saveTime) {
        put(key, CacheHelper.parcelable2Bytes(value), saveTime);
    }

    /**
     * 缓存中读取Parcelable
     *
     * @param key     键
     * @param creator 建造器
     * @param <T>     具体的类型
     * @return 存在且没过期返回对应值，否则返回{@code null}
     */
    public <T> T getParcelable(@NonNull String key, @NonNull Parcelable.Creator<T> creator) {
        return getParcelable(key, creator, null);
    }

    /**
     * 缓存中读取Parcelable
     *
     * @param key          键
     * @param creator      建造器
     * @param defaultValue 默认值
     * @param <T>          具体的类型
     * @return 存在且没过期返回对应值，否则返回默认值{@code defaultValue}
     */
    public <T> T getParcelable(@NonNull String key, @NonNull Parcelable.Creator<T> creator, T defaultValue) {
        byte[] bytes = getBytes(key);
        if (bytes == null) return defaultValue;
        return CacheHelper.bytes2Parcelable(bytes, creator);
    }

    /**
     * 缓存中写入Serializable
     *
     * @param key   键
     * @param value 值
     */
    public void put(@NonNull String key, @NonNull Serializable value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入Serializable
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    public void put(@NonNull String key, @NonNull Serializable value, int saveTime) {
        put(key, CacheHelper.serializable2Bytes(value), saveTime);
    }

    /**
     * 缓存中读取Serializable
     *
     * @param key 键
     * @return 存在且没过期返回对应值，否则返回{@code null}
     */
    public Object getSerializable(@NonNull String key) {
        return getSerializable(key, null);
    }

    /**
     * 缓存中读取Serializable
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在且没过期返回对应值，否则返回默认值{@code defaultValue}
     */
    public Object getSerializable(@NonNull String key, Object defaultValue) {
        byte[] bytes = getBytes(key);
        if (bytes == null) return defaultValue;
        return CacheHelper.bytes2Object(getBytes(key));
    }

    /**
     * 获取缓存大小
     * <p>单位：字节</p>
     *
     * @return 缓存大小
     */
    public long getCacheSize() {
        return mCacheManager.getCacheSize();
    }

    /**
     * 获取缓存个数
     *
     * @return 缓存个数
     */
    public int getCacheCount() {
        return mCacheManager.getCacheCount();
    }

    /**
     * 根据键值移除缓存
     *
     * @param key 键
     * @return {@code true}: 移除成功<br>{@code false}: 移除失败
     */
    public boolean remove(@NonNull String key) {
        return mCacheManager.removeByKey(key);
    }

    /**
     * 清除所有缓存
     *
     * @return {@code true}: 清除成功<br>{@code false}: 清除失败
     */
    public boolean clear() {
        return mCacheManager.clear();
    }

    /**
     * 缓存管理器
     */
    private class CacheManager {
        private final AtomicLong cacheSize;
        private final AtomicInteger cacheCount;
        private final long sizeLimit;
        private final int countLimit;
        private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<File, Long>());
        private final File cacheDir;

        /**
         * 构造行数
         *
         * @param cacheDir   缓存目录
         * @param sizeLimit  大小限制
         * @param countLimit 数量限制
         */
        private CacheManager(File cacheDir, long sizeLimit, int countLimit) {
            this.cacheDir = cacheDir;
            this.sizeLimit = sizeLimit;
            this.countLimit = countLimit;
            cacheSize = new AtomicLong();
            cacheCount = new AtomicInteger();
            calculateCacheSizeAndCacheCount();
        }

        /**
         * 计算大小和数量
         */
        private void calculateCacheSizeAndCacheCount() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int size = 0;
                    int count = 0;
                    final File[] cachedFiles = cacheDir.listFiles();
                    if (cachedFiles != null) {
                        for (File cachedFile : cachedFiles) {
                            size += cachedFile.length();
                            count += 1;
                            lastUsageDates.put(cachedFile, cachedFile.lastModified());
                        }
                        cacheSize.getAndAdd(size);
                        cacheCount.getAndAdd(count);
                    }
                }
            }).start();
        }

        /**
         * 获取缓存大小
         *
         * @return 缓存大小
         */
        private long getCacheSize() {
            return cacheSize.get();
        }

        /**
         * 获取缓存数量
         *
         * @return 缓存数量
         */
        private int getCacheCount() {
            return cacheCount.get();
        }

        /**
         * 获取文件
         *
         * @param key 文件缓存名称
         * @return 对应文件
         */
        private File getFileBeforePut(String key) {
            File file = new File(cacheDir, String.valueOf(key.hashCode()));
            if (file.exists()) {
                cacheCount.addAndGet(-1);
                cacheSize.addAndGet(-file.length());
            }
            return file;
        }

        /**
         * 获取文件
         *
         * @param key 文件缓存名称
         * @return 对应文件
         */
        private File getFileIfExists(String key) {
            File file = new File(cacheDir, String.valueOf(key.hashCode()));
            if (!file.exists()) return null;
            return file;
        }

        /**
         * 添加文件
         *
         * @param file 文件
         */
        private void put(File file) {
            cacheCount.addAndGet(1);
            cacheSize.addAndGet(file.length());
            while (cacheCount.get() > countLimit || cacheSize.get() > sizeLimit) {
                cacheSize.addAndGet(-removeOldest());
                cacheCount.addAndGet(-1);
            }
        }

        /**
         * 更新修改
         *
         * @param file 修改过的文件
         */
        private void updateModify(File file) {
            Long millis = System.currentTimeMillis();
            file.setLastModified(millis);
            lastUsageDates.put(file, millis);
        }

        /**
         * 删除文件
         *
         * @param key 文件缓存名称
         * @return
         */
        private boolean removeByKey(String key) {
            File file = getFileIfExists(key);
            if (file == null) return true;
            if (!file.delete()) return false;
            cacheSize.addAndGet(-file.length());
            cacheCount.addAndGet(-1);
            lastUsageDates.remove(file);
            return true;
        }

        /**
         * 清除缓存
         *
         * @return 是否清除完成
         */
        private boolean clear() {
            File[] files = cacheDir.listFiles();
            if (files == null || files.length <= 0) return true;
            boolean flag = true;
            for (File file : files) {
                if (!file.delete()) {
                    flag = false;
                    continue;
                }
                cacheSize.addAndGet(-file.length());
                cacheCount.addAndGet(-1);
                lastUsageDates.remove(file);
            }
            if (flag) {
                lastUsageDates.clear();
                cacheSize.set(0);
                cacheCount.set(0);
            }
            return flag;
        }

        /**
         * 移除旧的文件
         *
         * @return 移除的字节数
         */
        private long removeOldest() {
            if (lastUsageDates.isEmpty()) return 0;
            Long oldestUsage = Long.MAX_VALUE;
            File oldestFile = null;
            Set<Map.Entry<File, Long>> entries = lastUsageDates.entrySet();
            synchronized (lastUsageDates) {
                for (Map.Entry<File, Long> entry : entries) {
                    Long lastValueUsage = entry.getValue();
                    if (lastValueUsage < oldestUsage) {
                        oldestUsage = lastValueUsage;
                        oldestFile = entry.getKey();
                    }
                }
            }
            if (oldestFile == null) return 0;
            long fileSize = oldestFile.length();
            if (oldestFile.delete()) {
                lastUsageDates.remove(oldestFile);
                return fileSize;
            }
            return 0;
        }
    }

    /**
     * 缓存助手
     */
    private static class CacheHelper {

        static final int timeInfoLen = 14;

        /**
         * 给缓存加上过期时间
         *
         * @param second 秒
         * @param data   数据
         * @return 缓存数据
         */
        private static byte[] newByteArrayWithTime(int second, byte[] data) {
            byte[] time = createDueTime(second).getBytes();
            byte[] content = new byte[time.length + data.length];
            System.arraycopy(time, 0, content, 0, time.length);
            System.arraycopy(data, 0, content, time.length, data.length);
            return content;
        }

        /**
         * 创建过期时间
         *
         * @param second 秒
         * @return _$millis$_
         */
        private static String createDueTime(int second) {
            return String.format(Locale.getDefault(), "_$%010d$_", System.currentTimeMillis() / 1000 + second);
        }

        /**
         * 判断数据是否过期
         *
         * @param data 当前缓存
         * @return 是否过期
         */
        private static boolean isDue(byte[] data) {
            long millis = getDueTime(data);
            return millis != -1 && System.currentTimeMillis() > millis;
        }

        /**
         * 获取缓存的过期时间
         *
         * @param data 当前缓存
         * @return 过期时间
         */
        private static long getDueTime(byte[] data) {
            if (hasTimeInfo(data)) {
                String millis = new String(copyOfRange(data, 2, 12));
                try {
                    return Long.parseLong(millis) * 1000;
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
            return -1;
        }

        /**
         * 获取不带过期时间的缓存数据
         *
         * @param data 缓存数据
         * @return 缓存数据
         */
        private static byte[] getDataWithoutDueTime(byte[] data) {
            if (hasTimeInfo(data)) {
                return copyOfRange(data, timeInfoLen, data.length);
            }
            return data;
        }

        /**
         * 限制范围复制缓存
         *
         * @param original 原始数据
         * @param from     起始点
         * @param to       终结点
         * @return 新数据
         */
        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0) throw new IllegalArgumentException(from + " > " + to);
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }

        /**
         * 判断当前缓存是否包含过期时间
         *
         * @param data
         * @return
         */
        private static boolean hasTimeInfo(byte[] data) {
            return data != null
                    && data.length >= timeInfoLen
                    && data[0] == '_'
                    && data[1] == '$'
                    && data[12] == '$'
                    && data[13] == '_';
        }

        /**
         * 从二进制数据中写文件
         *
         * @param file  文件
         * @param bytes 数据源
         */
        private static void writeFileFromBytes(File file, byte[] bytes) {
            FileChannel fc = null;
            try {
                fc = new FileOutputStream(file, false).getChannel();
                fc.write(ByteBuffer.wrap(bytes));
                fc.force(true);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(fc);
            }
        }

        /**
         * 将文件读取到二进制数组中
         *
         * @param file 文件
         * @return 读取结果
         */
        private static byte[] readFile2Bytes(File file) {
            FileChannel fc = null;
            try {
                fc = new RandomAccessFile(file, "r").getChannel();
                int size = (int) fc.size();
                MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
                byte[] data = new byte[size];
                mbb.get(data, 0, size);
                return data;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                CloseUtils.closeIO(fc);
            }
        }

        /**
         * String转换为二进制
         *
         * @param string 原始字符串
         * @return 转换结果
         */
        private static byte[] string2Bytes(String string) {
            if (string == null) return null;
            return string.getBytes();
        }

        /**
         * 二进制转换为String
         *
         * @param bytes 数据源
         * @return 转换结果
         */
        private static String bytes2String(byte[] bytes) {
            if (bytes == null) return null;
            return new String(bytes);
        }

        /**
         * 将JsonObject转换为二进制
         *
         * @param jsonObject JsonObject
         * @return 转换结果
         */
        private static byte[] jsonObject2Bytes(JSONObject jsonObject) {
            if (jsonObject == null) return null;
            return jsonObject.toString().getBytes();
        }

        /**
         * 将二进制转换为JsonObject
         *
         * @param bytes 数据源
         * @return 转换结果
         */
        private static JSONObject bytes2JSONObject(byte[] bytes) {
            if (bytes == null) return null;
            try {
                return new JSONObject(new String(bytes));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 将JsonArray转换为二进制
         *
         * @param jsonArray JsonArray
         * @return 转换结果
         */
        private static byte[] jsonArray2Bytes(JSONArray jsonArray) {
            if (jsonArray == null) return null;
            return jsonArray.toString().getBytes();
        }

        /**
         * 将二进制转换为JsonArray
         *
         * @param bytes 数据源
         * @return 转换结果
         */
        private static JSONArray bytes2JSONArray(byte[] bytes) {
            if (bytes == null) return null;
            try {
                return new JSONArray(new String(bytes));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Parcelable转换为二进制
         *
         * @param parcelable Parcelable
         * @return 转换结果
         */
        private static byte[] parcelable2Bytes(Parcelable parcelable) {
            if (parcelable == null) return null;
            Parcel parcel = Parcel.obtain();
            parcelable.writeToParcel(parcel, 0);
            byte[] bytes = parcel.marshall();
            parcel.recycle();
            return bytes;
        }

        /**
         * 将二进制转换为Parcelable
         *
         * @param bytes   数据源
         * @param creator Parcelable构造器
         * @param <T>     具体类型对象
         * @return 具体对象
         */
        private static <T> T bytes2Parcelable(byte[] bytes, Parcelable.Creator<T> creator) {
            if (bytes == null) return null;
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);
            T result = creator.createFromParcel(parcel);
            parcel.recycle();
            return result;
        }

        /**
         * 将Serializable转换为二进制
         *
         * @param serializable Serializable
         * @return 转换结果
         */
        private static byte[] serializable2Bytes(Serializable serializable) {
            if (serializable == null) return null;
            ByteArrayOutputStream baos;
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(baos = new ByteArrayOutputStream());
                oos.writeObject(serializable);
                return baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                CloseUtils.closeIO(oos);
            }
        }

        /**
         * 将二进制转换为Object
         *
         * @param bytes 数据源
         * @return 转换结果
         */
        private static Object bytes2Object(byte[] bytes) {
            if (bytes == null) return null;
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                CloseUtils.closeIO(ois);
            }
        }

        /**
         * 将图片转换为二进制
         *
         * @param bitmap 图片
         * @return 转换结果
         */
        private static byte[] bitmap2Bytes(Bitmap bitmap) {
            if (bitmap == null) return null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        /**
         * 将二进制转换为图片
         *
         * @param bytes 数据源
         * @return 转换结果
         */
        private static Bitmap bytes2Bitmap(byte[] bytes) {
            return (bytes == null || bytes.length == 0) ? null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

        /**
         * 将Drawable转换为二进制
         *
         * @param drawable Drawable
         * @return 转换结果
         */
        private static byte[] drawable2Bytes(Drawable drawable) {
            return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable));
        }

        /**
         * 将二进制转换为Drawable
         *
         * @param bytes 数据源
         * @return 转换结果
         */
        private static Drawable bytes2Drawable(byte[] bytes) {
            return bytes == null ? null : bitmap2Drawable(bytes2Bitmap(bytes));
        }

        /**
         * Drawable转换为Bitmap
         *
         * @param drawable Drawable
         * @return Bitmap
         */
        private static Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }
            Bitmap bitmap;
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1,
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        /**
         * Bitmap转换为Drawable
         *
         * @param bitmap Bitmap
         * @return Drawable
         */
        private static Drawable bitmap2Drawable(Bitmap bitmap) {
            return bitmap == null ? null : new BitmapDrawable(ctx.getResources(), bitmap);
        }
    }

    /**
     * 判断字符串是空串
     *
     * @param s 数据源
     * @return 是否是空串
     */
    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
