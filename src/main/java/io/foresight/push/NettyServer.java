package io.foresight.push;

import io.foresight.push.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class NettyServer implements ApplicationRunner {

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;

  private final int port = 8001;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    bossGroup = new NioEventLoopGroup();
    workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new EchoServerHandler());
      ChannelFuture f = b.bind(8010).sync();
      System.err.println("Ready for 0.0.0.0:8010");
      f.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }


//    ServerBootstrap bootstrap = new ServerBootstrap();
//    bootstrap.group(bossGroup, workGroup) // [2]
//        .channel(NioServerSocketChannel.class)
////        .handler(bootstrapHandler) // [3]
//        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
//        .option(ChannelOption.SO_BACKLOG, 500) // [4]
//        .childOption(ChannelOption.TCP_NODELAY, true) // [5]
//        .childOption(ChannelOption.SO_LINGER, 0) // [6]
//        .childOption(ChannelOption.SO_KEEPALIVE, true) // [7]
//        .childOption(ChannelOption.SO_REUSEADDR, true); // [8]
////        .childHandler(webChannelInitializer); // [9]
//
//    ChannelFuture channelFuture = bootstrap.bind(port).sync();
//    channelFuture.channel().closeFuture().sync();


    log.info(" netty server run !!");
  }

  @PreDestroy
  public void shutdown() throws InterruptedException {
    log.info(" netty server shutdown !!");

    if(bossGroup != null){
      bossGroup.shutdownGracefully().sync();
    }
    if(workerGroup != null){
      workerGroup.shutdownGracefully().sync();
    }
  }
}
