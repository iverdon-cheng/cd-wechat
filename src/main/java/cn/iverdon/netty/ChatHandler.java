package cn.iverdon.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author iverdon
 * @date 2020/11/18 13:16
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        //1.获取客户端发来的消息
        //2.判断消息类型，根据不同的类型来处理不同的业务
        //  2.1 当websocket第一次open的时候，初始化channel，把用户的channel和userId关联起来
        //  2.2 聊天类型的消息，把聊天记录保存到数据库 标记消息的签收状态[未签收]
        //  2.3 签收消息的类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
        //  2.4 心跳类型的消息


    }

    /**
     * 获取客户端的channel，并且放到ChannelGroup中去进行管理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        String channelId = ctx.channel().id().asShortText();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
       //发生异常之后关闭连接(关闭channel)，随后从ChannelGroup中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
