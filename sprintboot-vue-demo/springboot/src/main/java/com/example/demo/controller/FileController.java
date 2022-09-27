package com.example.demo.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.demo.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${server.port}")
    private String port;

    private static final String ip = "http://localhost";
    @PostMapping("/upload")
    public  Result<?> upload(MultipartFile file) throws IOException {
        System.out.println("成功进入upload");
        String fileOriginName = file.getOriginalFilename();//获取源文件名称
        //获取上传的路径
        String flag = IdUtil.fastSimpleUUID(); // 随机的uuid
        String uniqueName = flag+"_"+fileOriginName;
        String rootFilePath = System.getProperty("user.dir")+"/springboot/src/main/resources/files/"+uniqueName;
        //写入上传的路径
        FileUtil.writeBytes(file.getBytes(),rootFilePath);
        return Result.success(ip+":"+port+ "/files/download/" + flag,flag);
    }
    @PostMapping("/display/{flag}")
    public  Result<?> display(MultipartFile file,@PathVariable String flag) throws IOException {
        System.out.println("成功进入display");
        String fileOriginName = file.getOriginalFilename();//获取源文件名称
        //获取上传的路径
        String uniqueName = flag+"_"+fileOriginName;
        String rootFilePath = System.getProperty("user.dir")+"/springboot/src/main/resources/files/"+uniqueName;
        //写入上传的路径
        FileUtil.writeBytes(file.getBytes(),rootFilePath);
        return Result.success(ip+":"+port+ "/files/download/" + flag,flag);
    }
    @GetMapping("/download/{flag}")
    public void download(@PathVariable String flag,HttpServletResponse response) throws IOException {
        OutputStream os;
        String basePath = System.getProperty("user.dir")+"/springboot/src/main/resources/files/";
        List<String> fileName = FileUtil.listFileNames(basePath);//获取所有文件名
        String avatar = fileName.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        try{
            if (StrUtil.isNotBlank(avatar)){
                response.addHeader("Content-Disposition","attachment;fileName="+ URLEncoder.encode(avatar,"UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(basePath+avatar);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
