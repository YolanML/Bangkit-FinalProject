package com.example.dampingi.utils

import android.util.Log
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

class RestrictedSocketFactory(private val mSendBufferSize: Int) : SocketFactory() {

    override fun createSocket(): Socket {
        return updateSendBufferSize(Socket())
    }


    override fun createSocket(host: String?, port: Int): Socket {
        return updateSendBufferSize(Socket(host, port))
    }


    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket {
        return updateSendBufferSize(Socket(host, port, localHost, localPort))
    }


    override fun createSocket(host: InetAddress?, port: Int): Socket {
        return updateSendBufferSize(Socket(host, port))
    }


    override fun createSocket(
        address: InetAddress?,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ): Socket {
        return updateSendBufferSize(Socket(address, port, localAddress, localPort))
    }

    @Throws(IOException::class)
    private fun updateSendBufferSize(socket: Socket): Socket {
        socket.setSendBufferSize(mSendBufferSize)
        return socket
    }

    companion object {
        private val TAG = RestrictedSocketFactory::class.java.simpleName
    }

    init {
        try {
            val socket = Socket()
            Log.w(
                TAG, java.lang.String.format(
                    "Changing SO_SNDBUF on new sockets from %d to %d.",
                    socket.getSendBufferSize(), mSendBufferSize
                )
            )
        } catch (e: SocketException) {
            //
        }
    }
}