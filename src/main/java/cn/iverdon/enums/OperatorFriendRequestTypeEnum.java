package cn.iverdon.enums;

/**
 * 
 * @Description: 添加好友前置状态 枚举
 */
public enum OperatorFriendRequestTypeEnum {

	IGNORE(0,"忽略"),
	PASS(1,"通过");

	public final Integer type;
	public final String msg;

	OperatorFriendRequestTypeEnum(Integer type, String msg){
		this.type = type;
		this.msg = msg;
	}
	
	public Integer getType() {
		return type;
	}  
	
	public static String getMsgByType(Integer type) {
		for (OperatorFriendRequestTypeEnum operType : OperatorFriendRequestTypeEnum.values()) {
			if (operType.getType() == type) {
				return operType.msg;
			}
		}
		return null;
	}
	
}
