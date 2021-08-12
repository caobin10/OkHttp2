package org.chuck.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 15-10-30.
 */
public class StorageUtil {

    public static String readAssets(Context context,String filePath,String charset){
        try {
            AssetManager manager=context.getAssets();
            InputStream data=manager.open(filePath);
            return readFile(data,charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream readRaw(Context context,int id){
        InputStream data=context.getResources().openRawResource(id);
        return data;
    }
    public static String readAppFile(Context context,String filePath,String charset){
        try {
            //获取APP的file中文件
//            File file=context.getFileStreamPath(filePath);
            FileInputStream data=context.openFileInput(filePath);
            return readFile(data,charset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFile(File file,String charset){
        StringBuilder builder=null;
        try {
            boolean isInit=false;
            FileInputStream fis=new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, charset==null?"utf-8":charset);
            BufferedReader buffReader = new BufferedReader(inputStreamReader);
            String line="";
            while ((line=buffReader.readLine())!=null){
                if(!isInit){builder=new StringBuilder();isInit=true;}
                builder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder==null?null:builder.toString();
    }



    public static String readFile(InputStream data,String charset){
        try {
            byte[] buf = new byte[data.available()];
            return new String(buf,charset);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }





    public static void writeAppFile(Context context,InputStream data,String filePath){
        try {
            FileOutputStream fos=context.openFileOutput(filePath, ((Activity) context).MODE_PRIVATE);
            int readLen;
            byte[] buf = new byte[1024];
            while ((readLen=data.read(buf))>0) {
                fos.write(buf, 0, readLen);
            }
            data.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeAppFile(Context context,InputStream data,String filePath,boolean append){
        //获取APP的file中文件
//        File file=new File(context.getFilesDir(),filePath);
        File file=context.getFileStreamPath(filePath);
        writeFile(data,file,append);
    }
    public static void writeAppFile(Context context,byte[] data,String filePath,boolean append){
        //获取APP的file中文件
//        File file=new File(context.getFilesDir(),filePath);
        File file=context.getFileStreamPath(filePath);
        writeFile(data,file,append);
    }


    public static void writeFile(byte[] data,File file,boolean append){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file,append);
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(InputStream data,File file,boolean append){
        try {
            FileOutputStream fos = new FileOutputStream(file,append);
            int readLen;
            byte[] buf = new byte[1024];
            while ((readLen=data.read(buf))>0) {
                fos.write(buf, 0, readLen);
            }
            data.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeFile(InputStream data,File file,boolean append,OnProgressListener listener){
        try {
            int length=data.available();
            FileOutputStream fos = new FileOutputStream(file,append);
            int readCount = 0;
            int readLen;
            byte[] buf = new byte[1024];
            while ((readLen=data.read(buf))>0) {
                fos.write(buf, 0, readLen);
                readCount += readLen;
                listener.onProgress(readCount, length);
            }
            data.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteFile(File file){
        boolean isSuccess=true;
        if(file.isDirectory()){
            File[] files=file.listFiles();
            for(File f:files){
                if(!isSuccess){
                    break;
                }
                isSuccess=f.delete();
            }
        }else{
            isSuccess=file.delete();
        }
        return isSuccess;
    }


    /**
     * 采用随机方式读取文件【未完成的方法】
     * @param file
     * @param charset
     * @return
     */
    public static byte[] readFileRandom(File file,String charset){
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
//            RandomAccessFile raf = new RandomAccessFile(file, "r");
//            MappedByteBuffer mbb = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,0,6000);
//            Charset inCharset = Charset.forName("GBK");
//            Charset outCharset = Charset.forName("UTF-8");
//            CharBuffer cb = inCharset.decode(mbb);
//            ByteBuffer outbb = outCharset.encode(cb);
//            CharSequence str = new String(outbb.array());
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static String getEncode(File file) throws Exception{
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        int p = (bis.read() << 8) + bis.read();
        bis.close();
        String encode = null;
        switch (p) {
            case 0xefbb:
                encode = "UTF-8";
                break;
            case 0xfffe:
                encode = "Unicode";
                break;
            case 0xfeff:
                encode = "UTF-16BE";
                break;
            default:
                encode = "GBK";
        }
        return encode;
    }

//    public void xx(InputStream is){
//        is.mark(64);
//        byte[] bytes = new byte[64];
//        int length = is.read(bytes);
//        is.reset();
//        String encoding = "US-ASCII";
//        int start = 0;
//        if (length > 1) {
//            if ((bytes[0] == (byte) 0xFF) && (bytes[1] == (byte) 0xFE)) {
//                encoding = "UTF-16LE";
//                start = 2;
//                length -= length & 1;
//            }
//            if ((bytes[0] == (byte) 0xFE) && (bytes[1] == (byte) 0xFF)) {
//                encoding = "UTF-16BE";
//                start = 2;
//                length -= length & 1;
//            }
//            if (length > 2) {
//                if ((bytes[0] == (byte) 0xEF) && (bytes[1] == (byte) 0xBB)
//                        && (bytes[2] == (byte) 0xBF)) {
//                    encoding = "UTF-8";
//                    start = 3;
//                }
//                if (length > 3) {
//                    if ((bytes[0] == (byte) 0x00) && (bytes[1] == (byte) 0x00)
//                            && (bytes[2] == (byte) 0xFE)
//                            && (bytes[3] == (byte) 0xFF)) {
//                        encoding = "UTF-32BE";
//                        start = 4;
//                        length -= length & 3;
//                    }
//                    if ((bytes[0] == (byte) 0xFF) && (bytes[1] == (byte) 0xFE)
//                            && (bytes[2] == (byte) 0x00)
//                            && (bytes[3] == (byte) 0x00)) {
//                        encoding = "UTF-32LE";
//                        start = 4;
//                        length -= length & 3;
//                    }
//                }
//            }
//        }
//
//        String header = new String(bytes, start, length - start, encoding);
//    }


    public boolean externStorageEnabled(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return true;
        }
        return false;
    }



    public interface OnProgressListener{
        public void onProgress(long bytes, long contentLength);
    }
}
