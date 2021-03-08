//package cn.jarkata.dts.server;
//
//import cn.jarkata.commons.concurrent.ThreadPoolFactory;
//import cn.jarkata.dts.common.utils.TransportUtils;
//import cn.jarkata.dts.config.ServerConfig;
//import cn.jarkata.dts.handler.DataTransferHandler;
//import cn.jarkata.dts.handler.JarkataChannelInitializer;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.buffer.ByteBufAllocator;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.InetSocketAddress;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//
//public class NettyServer {
//
//    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
//
//    public int port;
//
//    public NettyServer(int port) {
//        this.port = port;
//    }
//
//    public void start() {
//        ServerConfig serverConfig = new ServerConfig();
//        EventLoopGroup eventLoopGroup = TransportUtils.getEventGroup(0, "boss-group");
//        EventLoopGroup workLoopGroup = TransportUtils.getEventGroup(serverConfig.getWorkThreads(), "work-group");
//        List<ChannelHandler> channelHandlerList = getChannelHandlerList(serverConfig);
//
//        ServerBootstrap bootstrap = new ServerBootstrap();
//        bootstrap.channel(TransportUtils.getServerSocketChannel());
//        bootstrap.group(eventLoopGroup, workLoopGroup);
//        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
//        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
//        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
//        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
//        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
//        bootstrap.childOption(ChannelOption.SO_SNDBUF, 8 * 1024);
//        bootstrap.childOption(ChannelOption.SO_RCVBUF, 8 * 1024);
//        bootstrap.childHandler(new JarkataChannelInitializer(channelHandlerList));
//        try {
//            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
//            future.addListener((listener) -> {
//                logger.info("启动结果：" + listener.isSuccess());
//            });
//        } catch (Exception e) {
//            logger.info("启动失败：", e);
//        } finally {
//            TransportUtils.closeEventLoopGroup(eventLoopGroup, workLoopGroup);
//        }
//    }
//
//
//    private List<ChannelHandler> getChannelHandlerList(ServerConfig config) {
//        ExecutorService handlerPoolExecutor = ThreadPoolFactory.newThreadPool("handler",
//                config.getIoThreads(), config.getIoThreads(), 10000,
//                60 * 1000, 60 * 1000);
//
//        return Arrays.asList(new ProtobufVarint32FrameDecoder(),
//                new DataTransferHandler(handlerPoolExecutor),
//                new ProtobufVarint32LengthFieldPrepender());
//    }
//}
