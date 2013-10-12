package com.janop.aweb.server;

import java.nio.channels.SocketChannel;

import net.jcip.annotations.Immutable;

@Immutable
class ChangeRequest {
  final SocketChannel channel;
  final int ops;

  ChangeRequest(SocketChannel socket, int ops) {
    super();
    this.channel = socket;
    this.ops = ops;
  }
}
