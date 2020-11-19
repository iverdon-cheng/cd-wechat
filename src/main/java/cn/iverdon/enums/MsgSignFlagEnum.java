package cn.iverdon.enums;

/**
 * @author iverdon
 * @date 2020/11/19 20:11
 * 消息签收状态 枚举
 */
public enum MsgSignFlagEnum {

    unsigned(0,"未签收"),
    signed(1,"签收");

    public final Integer type;
    public final String content;

    MsgSignFlagEnum(Integer type, String content){
        this.type = type;
        this.content = content;
    }

    public Integer getType() {
        return type;
    }
}
