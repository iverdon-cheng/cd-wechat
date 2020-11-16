package cn.iverdon.mapper;

import cn.iverdon.model.Users;
import cn.iverdon.model.vo.FriendRequestVO;
import cn.iverdon.model.vo.MyFriendsVO;
import cn.iverdon.utils.MyMapper;

import java.util.List;

/**
 * @author iverdon
 * @date 2020/11/13 15:44
 */
public interface UsersMapperCustom extends MyMapper<Users> {

    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    List<MyFriendsVO> queryMyFriends(String myId);
}
