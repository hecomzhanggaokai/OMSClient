package com.hecom.omsclient.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 混淆时需要保留的类
 * 常见使用场景:通过Gson,dbutils进行操作的相关model或bean
 * @author tianlupan 2015/11/15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoProguard {
}
