package cn.iverdon.service;

import cn.iverdon.mapper.MyFriendsMapper;
import cn.iverdon.mapper.UsersMapper;
import cn.iverdon.model.MyFriends;
import cn.iverdon.model.Users;

import cn.iverdon.utils.FastDFSClient;
import cn.iverdon.utils.FileUtils;
import cn.iverdon.utils.QRCodeUtils;
import cn.iverdon.utils.UsersUtil;
import org.n3r.idworker.Sid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * @author iverdon
 * @date 2020/10/24 1:13
 */
@Service
public class UserService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    MyFriendsMapper myFriendsMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Value("${fdfs.web-server-url}")
    String nginxHost;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("查询数据库");
        Users users = usersMapper.loadUserByUsername(username);
        if (users == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        users.setFaceImage(UsersUtil.getTokenIn(users.getFaceImage()));
        users.setFaceImageBig(UsersUtil.getTokenIn(users.getFaceImageBig()));
        users.setQrcode(UsersUtil.getTokenIn(users.getQrcode()));
        return users;
    }

    public Users selectByUsername(String username) {
        logger.info("查询数据库");
        return usersMapper.selectByUsername(username);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Integer insert(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        //TODO 为每个用户生成一个唯一的二维码
        String qrCodePath = "D://qrcode//"+userId+"qrcode.png";
        //wechat_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath,"wechat_qrcode:"+user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setQrcode(qrCodeUrl);
        return usersMapper.insert(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateUserFaceById(String id, String url, String thumpImgUrl) {
        return usersMapper.updateUserFaceById(id,url,thumpImgUrl);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateNickname(String id, String nickName) {
        logger.info("nickname");
        int result = usersMapper.updateNickname(id, nickName);
        return usersMapper.queryUserById(id);
    }

    /**
     * 搜索朋友的前置条件
     * @param myUserId
     * @param friendName
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer preconditionSearchFriend(String myUserId, String friendName){
        //前置条件-1.搜索的用户不存在，返回【无此用户】

        return null;
    }


    public Users queryUserInfoByUsername(String username){

    }
}
