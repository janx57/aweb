package com.janx57.aweb.server;

import java.nio.channels.SocketChannel;

final class ChangeRequest {
  final SocketChannel channel;
  final int ops;

  ChangeRequest(SocketChannel socket, int ops) {
    super();
    this.channel = socket;
    this.ops = ops;
  }
}
