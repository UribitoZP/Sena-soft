package com.santaana.server;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class RestServer {

    private static final int PORT = 8443;
    private static HttpsServer server;

    public static void iniciar() {
        try {
            String ksPath = localizarKeystore();
            if (ksPath == null) {
                System.err.println("No se encontro keystore.p12, el servidor HTTPS no se iniciara.");
                return;
            }

            char[] pass = "sant@an@".toCharArray();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(ksPath)) {
                ks.load(fis, pass);
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, pass);

            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(kmf.getKeyManagers(), null, null);

            server = HttpsServer.create(new InetSocketAddress(PORT), 0);
            server.setHttpsConfigurator(new HttpsConfigurator(ssl));
            server.createContext("/auth/login",    new AuthHandler());
            server.createContext("/habitaciones",  new HabitacionesHandler());
            server.createContext("/reservas",      new ReservasHandler());
            server.createContext("/stats",         new StatsHandler());
            server.createContext("/reportes",      new ReportesHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Servidor HTTPS iniciado en puerto " + PORT);
        } catch (Exception e) {
            System.err.println("Error iniciando servidor HTTPS: " + e.getMessage());
        }
    }

    private static String localizarKeystore() {
        try {
            File dirClases = new File(
                RestServer.class
                    .getProtectionDomain().getCodeSource().getLocation()
                    .toURI()
            ).getAbsoluteFile();

            if (dirClases.isDirectory() && dirClases.getName().equalsIgnoreCase("bin")) {
                File ks = new File(dirClases.getParentFile(), "ssl/keystore.p12");
                if (ks.exists()) return ks.getAbsolutePath();
            }
        } catch (Exception ignored) {}

        File ks = new File("ssl/keystore.p12");
        if (ks.exists()) return ks.getAbsolutePath();

        if (generarKeystore(ks)) {
            System.out.println("Keystore generado automaticamente en " + ks.getAbsolutePath());
            return ks.getAbsolutePath();
        }
        return null;
    }

    private static boolean generarKeystore(File destino) {
        try {
            destino.getParentFile().mkdirs();
            ProcessBuilder pb = new ProcessBuilder(
                "keytool", "-genkeypair",
                "-alias", "santaana",
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-keystore", destino.getAbsolutePath(),
                "-storetype", "PKCS12",
                "-storepass", "sant@an@",
                "-dname", "CN=Hotel Santa Ana, OU=TI, O=Santa Ana, L=Bogota, C=CO",
                "-validity", "365"
            );
            return pb.start().waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void detener() {
        if (server != null) server.stop(0);
    }
}
