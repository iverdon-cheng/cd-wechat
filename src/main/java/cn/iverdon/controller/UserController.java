package cn.iverdon.controller;

import cn.iverdon.enums.OperatorFriendRequestTypeEnum;
import cn.iverdon.enums.SearchFriendsStatusEnum;
import cn.iverdon.model.MyFriends;
import cn.iverdon.model.RespBean;
import cn.iverdon.model.Users;
import cn.iverdon.model.vo.MyFriendsVO;
import cn.iverdon.service.impl.UserServiceImpl;
import cn.iverdon.utils.FastDFSClient;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private UserServiceImpl userServiceImpl;

    @Value("${fdfs.web-server-url}")
    String nginxHost;


    @PostMapping("/uploadFace")
    public RespBean uploadFace(MultipartFile file, String id, Authentication authentication) throws IOException {
        // 上传文件到fastd
        // fs
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
        if (userServiceImpl.updateUserFaceById(id,url,thumpImgUrl) == 1){
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
        Users result = userServiceImpl.updateNickname(id,nickName);
        result.setFaceImage(nginxHost+fastDFSClient.getToken(result.getFaceImage()));
        result.setFaceImageBig(nginxHost+fastDFSClient.getToken(result.getFaceImageBig()));
        return RespBean.ok(result);
    }

    /**
     * 搜索好友接口, 根据账号做匹配查询而不是模糊查询
     * @param friendName
     * @return
     */
    @PostMapping("/search")
    public RespBean searchUser(@RequestParam("friendUsername") String friendName){

        if (StringUtils.isBlank(friendName)){
            return RespBean.errorMsg("");
        }
        //前置条件-1.搜索的用户不存在，返回【无此用户】
        //前置条件-2.搜索的用户是自己，返回【不能添加自己】
        //前置条件-3.搜索的用户不存在，返回【无此用户】
        Users present = (Users)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer status = userServiceImpl.preconditionSearchFriend(present.getId(),friendName);
        if (status == SearchFriendsStatusEnum.SUCCESS.status){
            Users user = userServiceImpl.queryUserInfoByUsername(friendName);
            return RespBean.ok(user);
        }else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return RespBean.errorMsg(errorMsg);
        }
    }

    /**
     * @Description: 发送添加好友的请求
     */
    @PostMapping("/addFriendRequest")
    public RespBean addFriendRequest(@RequestParam("friendUsername") String friendName){
        if (StringUtils.isBlank(friendName)){
            return RespBean.errorMsg("");
        }
        //前置条件-1.搜索的用户不存在，返回【无此用户】
        //前置条件-2.搜索的用户是自己，返回【不能添加自己】
        //前置条件-3.搜索的用户不存在，返回【无此用户】
        Users present = (Users)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer status = userServiceImpl.preconditionSearchFriend(present.getId(),friendName);
        if (status == SearchFriendsStatusEnum.SUCCESS.status){
            userServiceImpl.sendFriendRequest(present.getId(),friendName);
        }else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return RespBean.errorMsg(errorMsg);
        }
        return RespBean.ok();
    }

    /**
     * @Description: 查询添加好友的请求
     */
    @PostMapping("/queryFriendRequest")
    public RespBean queryFriendRequest(){
        Users present = (Users)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return RespBean.ok(userServiceImpl.queryFriendRequestList(present.getId()));
    }

    @PostMapping("/operaFriendRequest")
    public RespBean operaFriendRequest(@RequestParam("friendId") String sendUserId, @RequestParam("operType") Integer operType){
        if (StringUtils.isBlank(sendUserId) || operType == null){
            return RespBean.errorMsg("");
        }
        // 1. 如果operType 没有对应的枚举值，则直接抛出空错误信息
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))){
            return RespBean.errorMsg("");
        }
        Users present = (Users)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (operType == OperatorFriendRequestTypeEnum.IGNORE.type){
            // 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
            userServiceImpl.deleteFriendRequest(present.getId(),sendUserId);
        }else if (operType == OperatorFriendRequestTypeEnum.PASS.type){
            // 3. 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表
            //	   然后删除好友请求的数据库表记录
            userServiceImpl.passFriendRequest(present.getId(),sendUserId);
        }
        return RespBean.ok(userServiceImpl.queryMyFriends(present.getId()));
    }

    @PostMapping("/myFriends")
    public RespBean myFriends(){
        //1.查询好友列表
        Users present = (Users)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<MyFriendsVO> myFriends = userServiceImpl.queryMyFriends(present.getId());
        return RespBean.ok(myFriends);
    }
}
