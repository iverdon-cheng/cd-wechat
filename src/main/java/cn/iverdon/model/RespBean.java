package cn.iverdon.model;

/**
 * @author iverdon
 * @date 2020/10/24 22:48
 */
public class RespBean {
    /**
     *  @Description: 自定义响应数据结构
     *  * 				这个类是提供给门户，ios，安卓，微信商城用的
     *  * 				门户接受此类数据后需要使用本类的方法转换成对于的数据类型格式（类，或者list）
     *  * 				其他自行处理
     *  * 				200：表示成功
     *  * 				500：表示错误，错误信息在msg字段中
     *  * 				501：bean验证错误，不管多少个错误都以map形式返回
     *  * 				502：拦截器拦截到用户token出错
     *  * 				555：异常抛出信息
     */
    private Integer status;
    private String msg;
    private Object obj;

    public RespBean(){}
    public RespBean(Integer status, String msg, Object obj){
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }
    public RespBean(Object obj){
        this.status = 200;
        this.msg = "OK";
        this.obj = obj;
    }

    public static RespBean build(Integer status, String msg, Object obj){
        return new RespBean(status,msg,obj);
    }

    public static RespBean ok(Object obj){
        return new RespBean(obj);
    }
    public static RespBean ok() {
        return new RespBean(null);
    }

    public static RespBean errorMsg(String msg) {
        return new RespBean(500, msg, null);
    }

    public static RespBean errorMap(Object data) {
        return new RespBean(501, "error", data);
    }

    public static RespBean errorTokenMsg(String msg) {
        return new RespBean(502, msg, null);
    }

    public static RespBean errorException(String msg) {
        return new RespBean(555, msg, null);
    }
    
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
