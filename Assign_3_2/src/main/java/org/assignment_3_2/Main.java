package org.assignment_3_2;

import java.net.*;
import java.util.Random;
import java.util.function.Predicate;


public class Main {

    public static void simulateNode1(NodeSelector node, String[] destinationIPs, int[] destinationPorts,
                                     Predicate<Integer>[] ackConditions, int[] ackPorts,
                                     String[] ackDestinationIPs) {
        int value = 0;
        int counter = 0;
        Random random = new Random();
        while (counter < 100) {
            value++;
            int index = random.nextInt(destinationIPs.length);
            String destIp = destinationIPs[index];
            int destPort = destinationPorts[index];
            node.sendData(String.valueOf(value), destIp, destPort);
            String receivingNode = destIp.equals(destinationIPs[0]) ? "N2" : "N3";
            System.out.println(node.getNodeName() + " sent " + value + " to " + destIp + ":" + destPort +
                    " (received by " + receivingNode + ")");
            for (int i = 0; i < ackConditions.length; i++) {
                if (ackConditions[i].test(value)) {
                    node.sendACK("ACK", ackDestinationIPs[i], ackPorts[i]);
                }
            }
            counter++;
            if (receivingNode.equals("N2") && value % 3 == 0) {
                System.out.println(receivingNode + " sent ACK to " + node.getNodeName() + " for value " + value);
                node.sendACK("ACK", node.getIpAddress(), ackPorts[0]);
            }
            if (receivingNode.equals("N3") && value % 5 == 0) {
                System.out.println(receivingNode + " sent ACK to " + node.getNodeName() + " for value " + value);
                node.sendACK("ACK", node.getIpAddress(), ackPorts[0]);
            }
        }
    }

    public static void main(String[] args) {
        NodeSelector N1 = new NodeSelector("Node 1", "127.0.0.1", getRandomPort());
        NodeSelector N2 = new NodeSelector("Node 2", "127.0.0.2", getRandomPort());
        NodeSelector N3 = new NodeSelector("Node 3", "127.0.0.3", getRandomPort());

        String[] destIps = {N2.getIpAddress(), N3.getIpAddress()};
        int[] destPorts = {N2.getNodePort(), N3.getNodePort()};
        Predicate<Integer>[] ackConditions = new Predicate[2];
        ackConditions[0] = Main::node2_ack;
        ackConditions[1] = Main::node3_ack;
        int[] ackPorts = {N1.getNodePort(), N1.getNodePort()};
        String[] ackDestIps = {N1.getIpAddress(), N1.getIpAddress()};

        int counter = 0;
        simulateNode1(N1, destIps, destPorts, ackConditions, ackPorts, ackDestIps);

        int ackReceived = 0;
        int expectedAcks = 100;
        while (ackReceived < expectedAcks && counter < 100) {
            String data = N1.receiveData();
            if (data != null && data.equals("ACK")) {
                ackReceived++;
            }
            counter++;
            if (ackReceived == 1) {
                System.out.println("The program stopped after receiving ACK for value 100.");
                break;
            }
        }
    }

    public static boolean node2_ack(int value) {
        return value % 3 == 0;
    }

    public static boolean node3_ack(int value) {
        return value % 5 == 0;
    }

    private static int getRandomPort() {
        return 1024 + new Random().nextInt(64512);
    }
}