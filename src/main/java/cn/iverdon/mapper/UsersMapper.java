package cn.iverdon.mapper;

import cn.iverdon.model.Users;
import cn.iverdon.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface UsersMapper extends MyMapper<Users> {

    Users loadUserByUsername(String username);

    Users selectByUsername(String username);

    int insert(Users user);

    int updateUserFaceById(@Param("id") String id, @Param("url") String url, @Param("thumpImgUrl")String thumpImgUrl);

    int updateNickname(@Param("id") String id, @Param("nickName") String nickName);

    Users queryUserById(String id);
}