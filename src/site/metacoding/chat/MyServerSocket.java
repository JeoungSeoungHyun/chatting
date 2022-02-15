package site.metacoding.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MyServerSocket {

    ServerSocket serverSocket; // 리스너(연결 = 세션)
    Socket socket; // 메시지 통신. os가 랜덤으로 알아서 정해준다.
    BufferedReader reader;

    // 추가(클라이언트에게 메시지 보내기)
    BufferedWriter writer;
    Scanner sc;

    public MyServerSocket() {
        try {
            // 1.서버소켓 생성(리스너)
            // well known port(0~1023) 사용하면 안됨!!
            // 내가 이미 사용하고 있는 포트번호도 사용하면 안됨!!
            serverSocket = new ServerSocket(1077);
            // System.out.println("서버 소켓이 1077번으로 기다리다가 main이 종료됨");
            socket = serverSocket.accept(); // while을 돌면서 클라이언트가 접속할 때까지 대기. time slicing이 걸려있다.
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            sc = new Scanner(System.in);

            new Thread(() -> {
                while (true) {
                    String inputData = sc.nextLine();
                    try {
                        writer.write(inputData + "\n");
                        writer.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            System.out.println("통신을 시작합니다.");
            while (true) {
                String inputData = reader.readLine();
                // System.out.println("클라이언트 연결됨");
                System.out.println("받은메세지 : " + inputData);
            }
        } catch (Exception e) {
            System.out.println("통신 오류 발생 :" + e.getMessage());
            // e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MyServerSocket();
        System.out.println("메인 종료");
    }
}
