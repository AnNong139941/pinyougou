package com.inso.core.controller.upload;


import com.inso.core.entity.Result;
import com.inso.core.utils.fastDFS.FastDFSClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class uploadController {

    //注入配置文件中的路径
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    /**
     * 商品文件上传
     * @param file
     * @return
     */
    @RequestMapping("uploadFile.do")
    public Result uploadFile(@RequestBody MultipartFile file) {
        try {
            //使用工具类进行上传
            String conf = "classpath:FastDFS/fdfs_client.conf";
            FastDFSClient fastDFSClient = new FastDFSClient(conf);

            //文件名
            String filename = file.getOriginalFilename();
           // System.out.println(filename);

            //使用commons包中的方法获取到扩展名
            String extName = FilenameUtils.getExtension(filename);
            String path = fastDFSClient.uploadFile(file.getBytes(), extName, null);

            //拼接最终的路径
            //path = "http://192.168.200.128/" + path;
            path = FILE_SERVER_URL + path;

            //返回路径用于页面回显图片
            return new Result(true, path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }

    }
}
