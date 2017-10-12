package com.btc.app.util;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
public class SqlSessionFactoryUtil {
    //SQLSessionFactory对象
    private static SqlSessionFactory sqlSessionFactory = null;
    //类线程锁
    private static final Class CLASS_LOCK= SqlSessionFactoryUtil.class;
    /**
     * 私有化构造函数
     */
    private SqlSessionFactoryUtil(){}
    /**
     * 构建SQLSessionFactory
     * @return
     */
    public static SqlSessionFactory initSqlSessionFactory(){
        String resource="mybatis-config.xml";
        InputStream inputstream=null;
        try{
            inputstream=Resources.getResourceAsStream(resource);
        }catch(IOException ex){
            Logger.getLogger(SqlSessionFactoryUtil.class.getName()).log(Level.ERROR, null, ex);
        }
        synchronized(CLASS_LOCK){
            if(sqlSessionFactory==null){
                sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputstream);
            }
        }
        return sqlSessionFactory;
    }
    /**
     * 打开SQLSession
     */
    public static SqlSession openSqlSession(){
        if(sqlSessionFactory==null){
            initSqlSessionFactory();
        }
        return sqlSessionFactory.openSession();
    }
}
