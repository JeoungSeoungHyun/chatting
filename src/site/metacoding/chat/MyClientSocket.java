package site.metacoding.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class MyClientSocket {

    Socket socket;
    BufferedWriter writer;
    Scanner sc;
    BufferedReader reader;

    public MyClientSocket() {
        try {
            // 새로운 소켓 생성(IP주소와 포트번호 필요)
            socket = new Socket("localhost", 1077);

            // 글을 쓰기 위한 버퍼 생성
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            // 글을 읽기 위한 버퍼 생성
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                while (true) {
                    try {
                        String inputData = reader.readLine();
                        // System.out.println("클라이언트 연결됨");
                        System.out.println("받은메세지 : " + inputData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            sc = new Scanner(System.in);

            // while문을 통해 지속적인 통신
            System.out.println("통신을 시작합니다.");
            while (true) {
                String inputData = sc.nextLine();
                writer.write(inputData + "\n");
                writer.flush();
            }
        } catch (Exception e) {
            System.out.println("통신 오류 발생 :" + e.getMessage());
            // e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MyClientSocket();
        System.out.println("메인 종료");
    }
}
