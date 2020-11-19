package cn.iverdon.enums;

/**
 * @author iverdon
 * @date 2020/11/19 19:02
 */
public enum MsgActionEnum {

    CONNECT(1,"第一次(或重连)初始化"),
    CHAT(2,"聊天消息"),
    SIGNED(3,"消息签收"),
    KEEPALIVE(4,"客户端保持心跳"),
    PULL_FRIEND(5,"拉取好友");

    public final Integer type;
    public final String content;

    MsgActionEnum(Integer type, String content){
        this.type = type;
        this.content = content;
    }

    public Integer getType(){ return type; }
}
