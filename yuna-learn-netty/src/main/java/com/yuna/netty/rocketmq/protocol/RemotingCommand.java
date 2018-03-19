package com.yuna.netty.rocketmq.protocol;

import com.alibaba.fastjson.annotation.JSONField;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yuna430 on 2018/3/19 0019.
 */
public class RemotingCommand {
    private static final int RPC_ONEWAY = 1; // 0, RPC
    private static final int RPC_TYPE = 0; // 0, REQUEST_COMMAND

    private static AtomicInteger requestId = new AtomicInteger(0);
    private int code;
    private int version = 0;
    private int opaque = requestId.getAndIncrement();
    private int flag = 0;
    private String remark;
    private HashMap<String, String> extFields;
    private SerializeType serializeTypeCurrentRPC = SerializeType.JSON;
    private transient byte[] body;

    public static void main(String[] args) {
        RemotingCommand command = new RemotingCommand();
        command.body = new String("hello").getBytes(Charset.forName("UTF-8"));
        ByteBuffer byteBuffer = command.encode();
        System.out.println(byteBuffer);
        System.out.println(command);

        RemotingCommand newCommond = RemotingCommand.decode(byteBuffer);
        System.out.println(newCommond);
    }

    public static RemotingCommand decode(ByteBuffer result) {
        // int length = result.limit();// header length
        int length = result.getInt();// header length
        int headerLength = result.getInt();
        int realHeaderLength = 0xFFFFFF & headerLength;
        System.out.println(realHeaderLength);
        byte[] headerData = new byte[realHeaderLength];
        result.get(headerData);

        RemotingCommand command = headerDecode(headerData, parseProtocolType(headerLength));

        int bodyLength = length - realHeaderLength - 4;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            result.get(bodyData);
        }

        command.setBody(bodyData);
        return command;
    }

    public static RemotingCommand headerDecode(byte[] headerData, SerializeType serializeType) {
        switch (serializeType) {
            case JSON:
                return RemotingJSONSerializable.decode(headerData, RemotingCommand.class);
            case ROCKETMQ:
            default:
                return null;
        }
    }

    /**
     * 从header length解析出编码类型
     *
     * @param source
     * @return
     */
    public static SerializeType parseProtocolType(int source) {
        byte code = (byte) (source >> 24 & 0xFF);
        return SerializeType.valueOf(code);
    }

    /**
     * 添加编码类型(第一个字节:编码类型，后三个字节：source)
     *
     * @param source
     * @param type
     * @return
     */
    public static byte[] markProtocolType(int source, SerializeType type) {
        byte[] result = new byte[4];

        result[0] = type.getCode();
        result[1] = (byte) (source >> 16 & 0xFF);
        result[2] = (byte) (source >> 8 & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }

    /**
     * Commond 编码
     * 4字节          1+3字节                          headerLength      totalLength-headerLength-4
     * totalLength + (serializeType + headerLength) + headerData    +   bodyData
     *
     * @return
     */
    public ByteBuffer encode() {
        // 1> header data size
        int length = 4;
        // 2> header data length
        byte[] headerData = this.headerEncode();
        length += headerData.length;
        // 3> body data length
        if (this.getBody() != null) {
            length += this.getBody().length;
        }
        ByteBuffer result = ByteBuffer.allocate(4 + length);
        // length
        result.putInt(length);
        // header length
        result.put(markProtocolType(headerData.length, serializeTypeCurrentRPC));
        // header data
        result.put(headerData);
        // body data
        if (this.getBody() != null) {
            result.put(this.getBody());
        }
        result.flip();
        return result;
    }

    /**
     * header编码
     * 4字节          1+3字节                          headerLength
     * totalLength + (serializeType + headerLength) + headerData
     *
     * @return
     */
    public ByteBuffer encodeHeader() {
        return encodeHeader(this.getBody() != null ? this.getBody().length : 0);
    }

    /**
     * header编码
     * 4字节          1+3字节                          headerLength
     * totalLength + (serializeType + headerLength) + headerData
     *
     * @return
     */
    public ByteBuffer encodeHeader(final int bodyLength) {
        // 1> header length size
        int length = 4;
        // 2> header data length
        byte[] headerData = headerEncode();

        length += headerData.length;
        // 3> body data length
        length += bodyLength;

        ByteBuffer result = ByteBuffer.allocate(4 + length - bodyLength);

        // length
        result.putInt(length);
        // header length
        result.put(markProtocolType(headerData.length, serializeTypeCurrentRPC));
        // header data
        result.put(headerData);
        result.flip();
        return result;
    }

    private byte[] headerEncode() {
        switch (serializeTypeCurrentRPC) {
            case JSON: // JSON序列方式
                return RemotingJSONSerializable.encode(this);
            case ROCKETMQ:// MQ 自定义
            default:
                return null;
        }
    }


    @JSONField(serialize = false)
    public RemotingCommandType getType() {
        if (this.isResponseType()) {
            return RemotingCommandType.RESPONSE_COMMAND;
        }
        return RemotingCommandType.REQUEST_COMMAND;
    }

    public void markOnewayRpc() {
        mark(RPC_ONEWAY);
    }

    @JSONField(serialize = false)
    public boolean isOnewayRpc() {
        return hasMarked(RPC_ONEWAY);
    }

    public void markResponseType() {
        mark(RPC_TYPE);
    }

    @JSONField(serialize = false)
    public boolean isResponseType() {
        return hasMarked(RPC_TYPE);
    }

    private void mark(int mask) {
        int bits = 1 << mask;
        flag |= bits;
    }

    private boolean hasMarked(int mask) {
        int bits = 1 << mask;
        return (this.flag & bits) == bits;
    }

    public int getCode() {
        return code;
    }

    public RemotingCommand setCode(int code) {
        this.code = code;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public RemotingCommand setVersion(int version) {
        this.version = version;
        return this;
    }

    public int getOpaque() {
        return opaque;
    }

    public RemotingCommand setOpaque(int opaque) {
        this.opaque = opaque;
        return this;
    }

    public int getFlag() {
        return flag;
    }

    public RemotingCommand setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public RemotingCommand setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public HashMap<String, String> getExtFields() {
        return extFields;
    }

    public RemotingCommand setExtFields(HashMap<String, String> extFields) {
        this.extFields = extFields;
        return this;
    }

    @JSONField(serialize = false)
    public SerializeType getSerializeTypeCurrentRPC() {
        return serializeTypeCurrentRPC;
    }

    public RemotingCommand setSerializeTypeCurrentRPC(SerializeType serializeTypeCurrentRPC) {
        this.serializeTypeCurrentRPC = serializeTypeCurrentRPC;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public RemotingCommand setBody(byte[] body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return "RemotingCommand [code=" + code + ", version=" + version + ", opaque=" + opaque + ", flag(B)="
                + Integer.toBinaryString(flag) + ", remark=" + remark + ", extFields=" + extFields + ", serializeTypeCurrentRPC="
                + serializeTypeCurrentRPC + ", body=" + new String(body, Charset.forName("UTF-8")) + "]";
    }

}
