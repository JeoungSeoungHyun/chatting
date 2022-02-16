package site.metacoding.chat_v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MyServerSocket {

    // 리스너(연결받기) - 메인스레드
    ServerSocket serverSocket;
    List<고객전담스레드> 고객리스트;

    // 메시지 받아서 보내기 (클라이언트 수마다 만들기)

    public MyServerSocket() {
        try {
            serverSocket = new ServerSocket(2000);
            고객리스트 = new Vector<>(); // 동기화가 처리된 ArrayList

            while (true) {
                // 포트 연결시 소켓 생성
                Socket socket = serverSocket.accept();
                System.out.println("클라이언트 연결됨");
                고객전담스레드 t = new 고객전담스레드(socket);
                고객리스트.add(t);
                System.out.println("고객리스트 크기 : " + 고객리스트.size());
                new Thread(t).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class 고객전담스레드 implements Runnable {

        // 익명클래스로 만들면 관리가 불가능해서
        Socket socket;
        BufferedReader reader;
        BufferedWriter writer;

        public 고객전담스레드(Socket socket) {
            this.socket = socket;

            // 버퍼 만들기
            try {
                reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String inputData = reader.readLine();
                    System.out.println("from 클라이언트 : " + inputData);

                    // for문 돌려서 List에 있는 다른 모든 클라이언트에게 메시지 전송
                    for (고객전담스레드 t : 고객리스트) {
                        t.writer.write(inputData + "\n");
                        t.writer.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        new MyServerSocket();
    }
}
