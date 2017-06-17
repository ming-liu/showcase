package com.ming.showcase.nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketChannelServer implements Runnable {

	private Selector selector;

	public SocketChannelServer() throws IOException {
		selector = Selector.open();
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.socket().bind(new InetSocketAddress("127.0.0.1", 8888), 1024);
		channel.configureBlocking(false);// no-blocking mode
		channel.register(selector, SelectionKey.OP_ACCEPT);
	}

	@Override
	public void run() {
		while (true) {
			try {
				int select = selector.select(100);
				if (select > 0) {
					// use selected keys ,and remove mannually 
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
		System.out.println("server process method entered.");
		if (selectionKey.isValid()) {
			if (selectionKey.isAcceptable()) {
				System.out.println("server isAcceptable");
				ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
				SocketChannel socketChannel = channel.accept();
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ);
			} else if (selectionKey.isReadable()) {
				System.out.println("server isReadable");
				SocketChannel channel = (SocketChannel) selectionKey.channel();
				byte[] byteArray = channelToBytes(channel);
				System.out.println("server ,received =" + new String(byteArray));
				channel.write(ByteBuffer.wrap("Hehe".getBytes()));
			}
		}
		System.out.println("server process finished");
	}

	public static byte[] channelToBytes(SocketChannel channel) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(16);
		//if client write 17 bytes,server read 16 bytes,then server side will receive another OP_READ 
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int read = channel.read(buf);
		if (read > 0) {
			buf.flip();
			if (buf.hasRemaining()) {
				byte[] dst = new byte[buf.limit()];
				buf.get(dst);
				bos.write(dst);
			}
			buf.clear();
		}
		return bos.toByteArray();
	}

	public static void main(String[] args) throws IOException {
		new Thread(new SocketChannelServer()).start();
	}

}
