package cn.iverdon.netty;

import java.io.Serializable;

/**
 * @author iverdon
 * @date 2020/11/19 19:17
 */
public class ChatMsg implements Serializable {

    private static final long serialVersionUID = -7003577966422550897L;

    private String senderId;
    private String receiverId;
    private String msg;
    private String msgId;       //用于消息的签收

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
