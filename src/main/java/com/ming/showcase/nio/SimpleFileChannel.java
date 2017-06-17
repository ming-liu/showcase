package com.ming.showcase.nio;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class SimpleFileChannel {

	public static void main(String[] args) throws Throwable {
		// simpleReadFromFileToBuffer();
		// scatterRead();
		transferFrom();
	}

	public static void transferFrom() throws Throwable {
		RandomAccessFile randomAccessFile = new RandomAccessFile("pom.xml", "rw");
		FileChannel channel = randomAccessFile.getChannel();

		RandomAccessFile toFile = new RandomAccessFile("to.xml", "rw");
		FileChannel toChannel = toFile.getChannel();
		
		long size = channel.size();
		System.out.println(size);
		System.out.println(toChannel.size());
		
		toChannel.transferFrom(channel, 0, size);
		System.out.println(toChannel.size());

		randomAccessFile.close();
		toFile.close();
	}

	public static void scatterRead() throws Throwable {
		RandomAccessFile randomAccessFile = new RandomAccessFile("pom.xml", "rw");
		FileChannel channel = randomAccessFile.getChannel();

		// create buffer with capacity of 32 bytes
		ByteBuffer buffer1 = ByteBuffer.allocate(32);
		ByteBuffer buffer2 = ByteBuffer.allocate(32);
		ByteBuffer[] buffers = { buffer1, buffer2 };
		long read = channel.read(buffers);
		System.out.println("read from file into buffers ,size = " + read);
		randomAccessFile.close();
	}

	public static void simpleReadFromFileToBuffer() throws Throwable {
		RandomAccessFile randomAccessFile = new RandomAccessFile("pom.xml", "rw");
		FileChannel channel = randomAccessFile.getChannel();

		// create buffer with capacity of 32 bytes
		ByteBuffer buffer = ByteBuffer.allocate(32);

		// read into buffer
		int read = channel.read(buffer);
		System.out.println("first read from file into buffer ,size = " + read);
		System.out.println("hasRemaining to read?" + buffer.hasRemaining());
		// make buffer ready for read
		buffer.flip();
		System.out.println("after flip,hasRemaining to read?" + buffer.hasRemaining());
		while (buffer.hasRemaining()) {
			System.out.print((char) buffer.get());
		}
		System.out.println();

		read = channel.read(buffer);
		System.out.println("read once more ,read size=" + read);

		// make buffer ready for write
		buffer.clear();
		read = channel.read(buffer);
		System.out.println("read once more after clear,size = " + read);

		randomAccessFile.close();
	}
}
