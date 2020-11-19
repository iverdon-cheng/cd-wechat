package cn.iverdon.netty;


import java.io.Serializable;

/**
 * @author iverdon
 * @date 2020/11/19 19:11
 */
public class DataContent implements Serializable {

    private static final long serialVersionUID = -1679074615579715059L;

    private Integer action;  // 动作类型
    private ChatMsg chatMsg; // 用户的聊天内容entity
    private String extend;   // 拓展字段

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
