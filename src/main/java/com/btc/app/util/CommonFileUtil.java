package com.btc.app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.io.Closeable;

public class CommonFileUtil {
    public static ArrayList<String> read(final File file){


        StringBuffer sb=new StringBuffer();
        ArrayList<String> resultList=new ArrayList<String>();
        FileInputStream fis=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        try{
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            String str=null;
            while((str=br.readLine())!=null){
                if(str.length()!=0){
                    //String ss=new String(str.getBytes());
                    resultList.add(str);
                }

            }
        }catch(IOException e){
            e.printStackTrace();
        }
        finally{
            closeQuietly(fis);
            closeQuietly(isr);
            closeQuietly(br);
        }

        if(resultList.size()>0)
            return resultList;
        else
            return null;
    }

    public static int write(final String filePath,final String content, final boolean append){
        File file=new File(filePath);
        FileOutputStream fos=null;
        OutputStreamWriter osw=null;
        if(content==null){
            System.out.println("empty content");
            return 0;
        }
        try{
            if(!(isFileExisted(file))){
                file.createNewFile();
            }
            fos=new FileOutputStream(filePath,append);
            osw=new OutputStreamWriter(fos,"UTF-8");
            osw.write(content);
            osw.flush();

        }catch(IOException e){
            e.printStackTrace();
            return 0;
        }finally{
            closeQuietly(osw);
            closeQuietly(fos);
        }
        return 1;
    }
    private static void closeQuietly(Closeable closeable){
        if(closeable!=null)
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    private static boolean isFileExisted(final File file){
        if(file.exists()&&file.isFile())
            return true;
        else
            return false;
    }
}
