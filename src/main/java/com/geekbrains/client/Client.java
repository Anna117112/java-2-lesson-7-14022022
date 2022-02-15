package com.geekbrains.client;

import com.geekbrains.CommonConstants;
import com.geekbrains.server.ServerCommandConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Scanner scanner;
// создаем контструктор
    public Client() {
        // считываем с консоли
        scanner = new Scanner(System.in);
        try {
// подключаемся к серверу и открываем новый поток на считывание сообщиний от клиента
            openConnection();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void openConnection() throws IOException {
// подключаемся к серверу
        initializeNetwork();
 // открываем новый поток
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
// запускаем бесконечный цикл
                    while (true) {
// считываем входящие сообщения от клиента
                        String messageFromServer = inputStream.readUTF();
// печатаем их в консоль
                        System.out.println(messageFromServer);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
// открываем новый поток в бесконечном потоке считываем собщение с консоли
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String text = scanner.nextLine();
// если клиент написал /end то  closeConnection() то закрываем чтение и отправку и подключение
                        if(text.equals(ServerCommandConstants.SHUTDOWN)) {
                            sendMessage(text);
                            closeConnection();
// в противном случае отправляем текст
                        } else {
                            sendMessage(text);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
// подключение к серверу
    private void initializeNetwork() throws IOException {
        socket = new Socket(CommonConstants.SERVER_ADDRESS, CommonConstants.SERVER_PORT);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

// отправляем сообщение
    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        System.exit(1);
    }

    public static void main(String[] args) {
        new Client();
    }
}
