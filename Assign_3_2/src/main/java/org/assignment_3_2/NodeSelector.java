package org.assignment_3_2;

import java.net.*;
import java.nio.charset.StandardCharsets;

class NodeSelector {
    private String nodeName;
    private String ipAddress;
    private DatagramSocket datagramSocket;
    private int nodePort;

    public NodeSelector(String nodeName, String ipAddress, int port) {
        this.nodeName = nodeName;
        this.ipAddress = ipAddress;
        try {
            this.datagramSocket = new DatagramSocket(port);
            this.nodePort = port;
            this.datagramSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            System.err.println("Error creating DatagramSocket: " + e.getMessage());
        }
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void sendData(String data, String destinationIp, int destinationPort) {
        try {
            byte[] sendData = data.getBytes(StandardCharsets.UTF_8);
            InetAddress destinationAddress = InetAddress.getByName(destinationIp);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationAddress, destinationPort);
            datagramSocket.send(sendPacket);
        } catch (Exception e) {
            System.err.println("Error sending data: " + e.getMessage());
        }
    }

    public void sendACK(String data, String destinationIp, int destinationPort) {
        try {
            byte[] ackData = data.getBytes(StandardCharsets.UTF_8);
            InetAddress destinationAddress = InetAddress.getByName(destinationIp);
            DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, destinationAddress, destinationPort);
            datagramSocket.send(ackPacket);
        } catch (Exception e) {
            System.err.println("Error sending ACK: " + e.getMessage());
        }
    }

    public String receiveData() {
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            datagramSocket.receive(receivePacket);
            return new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
        } catch (SocketTimeoutException e) {
            return null;
        } catch (Exception e) {
            System.err.println("Error receiving data: " + e.getMessage());
            return null;
        }
    }
}