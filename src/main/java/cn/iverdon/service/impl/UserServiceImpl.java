package cn.iverdon.service.impl;

import cn.iverdon.enums.SearchFriendsStatusEnum;
import cn.iverdon.mapper.FriendsRequestMapper;
import cn.iverdon.mapper.MyFriendsMapper;
import cn.iverdon.mapper.UsersMapper;
import cn.iverdon.mapper.UsersMapperCustom;
import cn.iverdon.model.FriendsRequest;
import cn.iverdon.model.MyFriends;
import cn.iverdon.model.Users;

import cn.iverdon.model.vo.FriendRequestVO;
import cn.iverdon.model.vo.MyFriendsVO;
import cn.iverdon.service.UserService;
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
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * @author iverdon
 * @date 2020/10/24 1:13
 */
@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

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
        Users user = queryUserInfoByUsername(friendName);
        if (user == null){
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }
        if (user.getId().equals(myUserId)){
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }
        Example mfe = new Example(MyFriends.class);
        Example.Criteria criteria = mfe.createCriteria();
        criteria.andEqualTo("myUserId",myUserId);
        criteria.andEqualTo("myFriendUserId",user.getId());
        MyFriends myFriends = myFriendsMapper.selectOneByExample(mfe);
        if (myFriends != null){
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }
        return SearchFriendsStatusEnum.SUCCESS.getStatus();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserInfoByUsername(String username){
        Example ue = new Example(Users.class);
        Example.Criteria criteria = ue.createCriteria();
        criteria.andEqualTo("username",username);
        Users user = usersMapper.selectOneByExample(ue);
        if (user != null){
            user.setFaceImage(UsersUtil.getTokenIn(user.getFaceImage()));
            user.setFaceImageBig(UsersUtil.getTokenIn(user.getFaceImageBig()));
        }
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String id, String friendName) {

        Users friend = queryUserInfoByUsername(friendName);

        // 1. 查询发送好友请求记录表
        Example fre = new Example(FriendsRequest.class);
        Example.Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId",id);
        frc.andEqualTo("acceptUserId",friend.getId());
        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(fre);
        if (friendsRequest == null) {
            // 2. 如果不是你的好友，并且好友记录没有添加，则新增好友请求记录
            String requestId = sid.nextShort();
            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(id);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
        List<FriendRequestVO> list = usersMapperCustom.queryFriendRequestList(acceptUserId);
        for (FriendRequestVO fr : list){
            fr.setSendFaceImage(UsersUtil.getTokenIn(fr.getSendFaceImage()));
        }
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String acceptUserId, String sendUserId) {
        Example fre = new Example(FriendsRequest.class);
        Example.Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId",sendUserId);
        frc.andEqualTo("acceptUserId",acceptUserId);
        friendsRequestMapper.deleteByExample(fre);
    }

    @Override
    public void passFriendRequest(String acceptUserId, String sendUserId) {
        saveFriends(sendUserId,acceptUserId);
        saveFriends(acceptUserId,sendUserId);
        deleteFriendRequest(acceptUserId,sendUserId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void saveFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        String recordId = sid.nextShort();
        myFriends.setId(recordId);
        myFriends.setMyFriendUserId(acceptUserId);
        myFriends.setMyUserId(sendUserId);
        myFriendsMapper.insert(myFriends);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryMyFriends(String myId) {
        List<MyFriendsVO> myFriends = usersMapperCustom.queryMyFriends(myId);
        for (MyFriendsVO friend : myFriends){
            friend.setFriendFaceImage(UsersUtil.getTokenIn(friend.getFriendFaceImage()));
        }
        return myFriends;
    }
}
