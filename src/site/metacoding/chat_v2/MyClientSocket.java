package site.metacoding.chat_v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class MyClientSocket {

    Socket socket;
    boolean isLogin = true;

    // 쓰기 스레드 필요 - 메인 스레드
    Scanner sc;
    BufferedWriter writer;

    // 읽기 스레드 필요 - 새로운 스레드
    BufferedReader reader;

    public MyClientSocket() {
        try {
            // 소켓 연결
            socket = new Socket("localhost", 2000);

            // 버퍼 달기
            sc = new Scanner(System.in);
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // 새로운스레드(읽기 전용)
            new Thread(new 읽기전담스레드()).start();

            // 메인스레드(쓰기 전용) - 메인스레드는 마지막에
            while (isLogin) {
                String keyboardInpuData = sc.nextLine();
                writer.write(keyboardInpuData + "\n"); // 버퍼에 담기
                writer.flush(); // 버퍼에 담긴 것을 stream으로 흘려보내기
            }
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("연결이 없습니다");
        }
    }

    class 읽기전담스레드 implements Runnable {

        @Override
        public void run() {
            try {
                while (isLogin) {
                    String inputData = reader.readLine();
                    System.out.println("받은 메시지 : " + inputData);
                }
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("연결 해제됨");
                isLogin = false;
            }
        }

    }

    public static void main(String[] args) {
        new MyClientSocket();
    }
}
