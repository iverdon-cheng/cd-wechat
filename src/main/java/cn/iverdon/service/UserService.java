package cn.iverdon.service;

import cn.iverdon.model.Users;
import cn.iverdon.model.vo.FriendRequestVO;
import cn.iverdon.model.vo.MyFriendsVO;
import cn.iverdon.netty.ChatMsg;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * @author iverdon
 * @date 2020/11/12 11:24
 */
public interface UserService extends UserDetailsService {

    public Users selectByUsername(String username);

    public Integer insert(Users user);

    public int updateUserFaceById(String id, String url, String thumpImgUrl);

    public Users updateNickname(String id, String nickName);

    public Integer preconditionSearchFriend(String myUserId, String friendName);

    public Users queryUserInfoByUsername(String username);

    void sendFriendRequest(String id, String friendName);

    /**
     * 查询好友请求
     * @param acceptUserId
     * @return
     */
    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /**
     * 删除好友记录
     * @param acceptUserId
     * @param sendUserId
     */
    void deleteFriendRequest(String acceptUserId, String sendUserId);

    /**
     * 通过好友记录
     * @param acceptUserId
     * @param sendUserId
     */
    void passFriendRequest(String acceptUserId, String sendUserId);

    List<MyFriendsVO> queryMyFriends(String myId);

    /**
     * @Description: 保存聊天消息到数据库
     */
    public String saveMsg(ChatMsg chatMsg);
}
