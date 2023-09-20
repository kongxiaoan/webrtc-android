package com.webrtc.android.websocket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

/**
 *
 * @author: kpa
 * @date: 2023/9/20
 * @description: web socket 管理类，用于充当信令服务器，交换数据
 */
class WebsocketClient {

    private val TAG = "WebsocketClient"
    private val websocketScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val client = OkHttpClient().newBuilder()
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .pingInterval(40, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private var request = Request.Builder()
        .url("ws://192.168.31.224:9000/ws")
        .build()

    private val ws = client.newWebSocket(request, WebsocketListenerImpl())

    /**
     * 业务流 根据改状态进行对应的业务操作
     */
    private val _sessionStateFlow = MutableStateFlow(WebRTCSessionState.Offline)
    val sessionStateFlow: StateFlow<WebRTCSessionState> = _sessionStateFlow

    /**
     * 信令服务器
     */
    private val _signalingCommandFlow = MutableSharedFlow<Pair<SignalingCommand, String>>()
    val signalingCommandFlow: SharedFlow<Pair<SignalingCommand, String>> = _signalingCommandFlow

    /**
     * 释放操作
     */
    fun dispose() {
        _sessionStateFlow.value = WebRTCSessionState.Offline
        websocketScope.cancel()
        ws.cancel()
    }

    private inner class WebsocketListenerImpl : WebSocketListener {
        constructor() : super() {
            Log.d(TAG, "constructor ")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d(TAG, "onClosed ")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            Log.d(TAG, "onClosing ")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.d(TAG, "onFailure ${t.message}")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.d(TAG, "onMessage ")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            Log.d(TAG, "onMessage")
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d(TAG, "onOpen ")
            webSocket.send("nihao")
        }
    }
}

enum class WebRTCSessionState {
    Active, // Offer and Answer messages has been sent
    Creating, // Creating session, offer has been sent
    Ready, // Both clients available and ready to initiate session
    Impossible, // We have less than two clients connected to the server
    Offline, // unable to connect signaling server
}

enum class SignalingCommand {
    STATE, // Command for WebRTCSessionState
    OFFER, // to send or receive offer
    ANSWER, // to send or receive answer
    ICE, // to send and receive ice candidates
}
