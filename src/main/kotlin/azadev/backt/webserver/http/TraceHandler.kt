package azadev.backt.webserver.http

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter


class TraceHandler : ChannelInboundHandlerAdapter()
{
	override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
		val buf = msg as? ByteBuf ?: return
		println("---------- TRACE")
		println(buf.toString(Charsets.UTF_8))
		println("---------- /TRACE")
		super.channelRead(ctx, msg)
	}
}
