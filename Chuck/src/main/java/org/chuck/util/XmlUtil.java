package org.chuck.util;

import android.util.Log;
import android.util.Xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 15-12-2.
 */
public class XmlUtil {
    private static DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
    private static XmlMapper mapper=new XmlMapper();
    static {
        mapper.setDateFormat(dateFormat);
    }
//
//    public static <T> T pullParseToObj(Reader reader,Class<T> cls) {
//        T obj = null;
//        try {
//            obj=mapper.readValue(reader,cls);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return obj;
//    }
//    public static <T> List<T> pullParseToObjs(InputStream is,Charset charset,Class<T> cls) {
//        Reader reader=new InputStreamReader(is, charset);
//        return pullParseToObjs(reader,cls);
//    }
//
//    public static <T> List<T> pullParseToObjs(Reader reader,Class<T> cls) {
//        List<T> objs = null;
//        try {
//            objs=mapper.readValue(reader,mapper.getTypeFactory().constructCollectionType(List.class, cls));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return objs;
//    }
//    public static void pullSerialize(){
//
//    }
}
