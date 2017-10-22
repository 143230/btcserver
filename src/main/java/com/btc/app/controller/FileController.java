package com.btc.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.btc.app.statistics.SystemStatistics;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

@Controller
@RequestMapping("/file/")
public class FileController {
    private static final SystemStatistics statistics = SystemStatistics.getInstance();

    private static final Logger logger = Logger.getLogger("FileController");
    private String android_version;
    private String ios_version;
    private String ios_version_desc;
    private String android_version_desc;

    @RequestMapping("/{fileName}.apk")
    public void download(HttpServletResponse response, HttpServletRequest request, @PathVariable("fileName") String fileName) throws Exception {

        String filePath = "/root/tomcat_files/" + fileName + ".apk";
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            //重置response
//            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            //设置http头信息的内容
//            response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            //解决中文文件名显示问题
            response.addHeader("Content-Disposition", "attachment;filename=" + new String((fileName+".apk").getBytes("gb2312"), "ISO8859-1"));
            //设置文件长度
            int fileLength = (int) file.length();
            response.setContentLength(fileLength);

            if (fileLength != 0) {
                InputStream inStream = new FileInputStream(file);
                byte[] buf = new byte[4096];

                //创建输出流
                ServletOutputStream servletOS = response.getOutputStream();
                int readLength;

                //读取文件内容并写入到response的输出流当中
                while ((readLength = inStream.read(buf)) != -1) {
                    servletOS.write(buf, 0, readLength);
                }
                //关闭输入流
                inStream.close();

                //刷新输出流缓冲
                servletOS.flush();

                //关闭输出流
                servletOS.close();
                logger.info(String.format("apk downloaded %s", getIpAddr(request)));
                statistics.add("download", 1);
            }
        } else {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("file \"" + fileName + ".apk\" not exist.");
            out.close();
        }
    }

    @RequestMapping(value = "/version", produces = "application/json; charset=utf-8")
    public @ResponseBody String getVersion(HttpServletRequest request) throws Exception {
        String device = request.getParameter("device");
        JSONObject json = new JSONObject();
        versionCheck();
        if(device == null){
            json.put("status", "error");
            json.put("message", "parameter of device needed");
            json.put("code", -1);
        }else if(device.equalsIgnoreCase("ios")){
            json.put("status", "success");
            json.put("message", ios_version);
            json.put("desc", ios_version_desc);
            json.put("download_url", "itms-apps://itunes.apple.com/cn/app/jie-zou-da-shi/id493901993?mt=8");
            json.put("code", 0);
        }else if(device.equalsIgnoreCase("android")){
            json.put("status", "success");
            json.put("message", android_version);
            json.put("desc", android_version_desc);
            json.put("download_url", "https://fengzhihen.com/btcapp/file/bidongjingling.apk");
            json.put("code", 0);
        }else{
            json.put("status", "error");
            json.put("message", "parameter of device not found");
            json.put("code", -2);
        }
        return json.toJSONString();
    }

    private void versionCheck() throws IOException {
        final String filepath = "/root/tomcat_files/version.conf";
        Properties prop = new Properties();
        prop.load(new FileInputStream(filepath));
        ios_version = prop.getProperty("ios_version");
        //new String(props.getProperty(key).getBytes("ISO8859-1"), encode);
        ios_version_desc = new String(prop.getProperty("ios_version_desc").getBytes("ISO8859-1"), "UTF-8");
        android_version = prop.getProperty("android_version");
        android_version_desc = new String(prop.getProperty("android_version_desc").getBytes("ISO8859-1"), "UTF-8");
    }

    /**
     * 获取访问者IP
     *
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     *
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request .getRemoteAddr()。
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) throws Exception{
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
// 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

}
