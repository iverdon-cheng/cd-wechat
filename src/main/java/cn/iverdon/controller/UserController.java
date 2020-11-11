package cn.iverdon.controller;

import cn.iverdon.model.RespBean;
import cn.iverdon.model.Users;
import cn.iverdon.service.UserService;
import cn.iverdon.utils.FastDFSClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.jar.JarOutputStream;

/**
 * @author iverdon
 * @date 2020/10/29 10:50
 */
@RestController
@RequestMapping("/u")
public class UserController {

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private UserService userService;

    @Value("${fdfs.web-server-url}")
    String nginxHost;

    @PostMapping("/uploadFace")
    public RespBean uploadFace(MultipartFile file, String id, Authentication authentication) throws IOException {
        // 上传文件到fastdfs
        System.out.println(id);
        System.out.println(file.getOriginalFilename());
        String url = fastDFSClient.uploadBase64(file);
        String token = fastDFSClient.getToken(url);
        System.out.println(token);

        // 获取缩略图的url
        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0]+thump+arr[1];

        //更新用户头像
        if (userService.updateUserFaceById(id,url,thumpImgUrl) == 1){
            Users user = (Users) authentication.getPrincipal();
            user.setFaceImage(nginxHost+fastDFSClient.getToken(thumpImgUrl));
            user.setFaceImageBig(nginxHost+fastDFSClient.getToken(url));
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,authentication.getCredentials(),authentication.getAuthorities()));
            return RespBean.ok((Users)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }
        return RespBean.errorMsg("更新失败！");
    }

    @PutMapping("/setNickname")
    public RespBean updateNickname(@RequestBody Map<String,Object> info){
        String id = (String) info.get("userId");
        String nickName = (String) info.get("nickname");
        Users result = userService.updateNickname(id,nickName);
        result.setFaceImage(nginxHost+fastDFSClient.getToken(result.getFaceImage()));
        result.setFaceImageBig(nginxHost+fastDFSClient.getToken(result.getFaceImageBig()));
        return RespBean.ok(result);
    }

    /**
     * 搜索好友接口, 根据账号做匹配查询而不是模糊查询
     * @param myUserId
     * @param friendName
     * @return
     */
    @PostMapping("/select")
    public RespBean searchUser(String myUserId, String friendName){

        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendName)){
            return RespBean.errorMsg("");
        }
        //前置条件-1.搜索的用户不存在，返回【无此用户】
        //前置条件-2.搜索的用户是自己，返回【不能添加自己】
        //前置条件-3.搜索的用户不存在，返回【无此用户】
        return RespBean.ok(result);
    }
}
