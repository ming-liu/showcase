package com.ming.showcase.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketChannelClient implements Runnable {

	private Selector selector;
	private SocketChannel socketChannel;

	public SocketChannelClient() throws IOException {
		selector = Selector.open();
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(new InetSocketAddress("127.0.0.1", 8888));
	}

	@Override
	public void run() {
		while (true) {
			try {
				int select = selector.select(1000);
				if (select > 0) {
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					while (iterator.hasNext()) {
						SelectionKey selectionKey = iterator.next();
						iterator.remove();
						process(selectionKey);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void process(SelectionKey selectionKey) throws IOException {
		if (selectionKey.isValid()) {
			SocketChannel channel = (SocketChannel) selectionKey.channel();
			if (selectionKey.isConnectable()) {
				boolean finishConnect = channel.finishConnect();
				channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				System.out.println("isConnectable,finishConnect=" + finishConnect);
			} else if (selectionKey.isReadable()) {
				byte[] bytes = SocketChannelServer.channelToBytes(channel);
				System.out.println("isReadable,received from server=" + new String(bytes));
			} else if (selectionKey.isWritable()) {
				System.out.println("isWritable");
				channel.write(ByteBuffer.wrap("1234567890123456abcdefg".getBytes()));
			}
		}

	}

	public static void main(String[] args) throws IOException {
		new Thread(new SocketChannelClient()).start();
	}
}
