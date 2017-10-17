package com.btc.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;

@Controller
@RequestMapping("/file/")
public class FileController {

    @RequestMapping("/{fileName}.apk")
    public void download(HttpServletResponse response, @PathVariable("fileName") String fileName) throws Exception {
        System.out.println(fileName);
        String filePath = "/root/files/" + fileName + ".apk";
        File file = new File(filePath);
        if (file.exists()) {
            //重置response
            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html;charset=utf-8");
            //设置http头信息的内容
            response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            //解决中文文件名显示问题
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1"));
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
            }
        } else {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("file \"" + fileName + "\" not exist.");
            out.close();
        }
    }
}
