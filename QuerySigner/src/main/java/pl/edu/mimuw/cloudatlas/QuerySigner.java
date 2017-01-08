package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import org.apache.log4j.varia.NullAppender;
import pl.edu.mimuw.cloudatlas.interpreter.QueryResult;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.interpreter.query.Yylex;
import pl.edu.mimuw.cloudatlas.interpreter.query.parser;
import spark.Spark;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static spark.Spark.port;


/**
 * Created by jks on 1/3/17.
 */
public class QuerySigner implements SigningInterface {
    HashMap<String, String> installedQueries = new HashMap<>();
    private final static String ENCRYPTION_ALGORITHM = "RSA";
    private final static int NUM_KEY_BITS = 1024;
    static KeyPair keyPair = null;
    static PrivateKey privateKey = null;
    static PublicKey publicKey = null;
    static Cipher signCipher;
    static Properties p = new Properties();
    static int apiport;
    public static void main(String [] args){
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());
        FileInputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            p.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String spvkey = "config.privatekey";
        String spukey = "config.publickey";
        File fv = new File(spvkey);
        File fu = new File(spukey);
        if(!fv.exists() || !fu.exists()){
            try {
                KeyPairGenerator keyGenerator = null;
                try {
                    keyGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return;
                }
                keyGenerator.initialize(NUM_KEY_BITS);
                keyPair = keyGenerator.generateKeyPair();
                privateKey = keyPair.getPrivate();
                publicKey = keyPair.getPublic();
                FileOutputStream fpv = new FileOutputStream(spvkey);
                FileOutputStream fpu = new FileOutputStream(spukey);
                fpv.write(privateKey.getEncoded());
                fpv.close();
                fpu.write(publicKey.getEncoded());
                fpu.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                byte[] bpv = Files.readAllBytes(Paths.get(spvkey));
                byte[] bpu = Files.readAllBytes(Paths.get(spukey));
                publicKey = keyFactory.generatePublic (new X509EncodedKeySpec(bpu));
                privateKey =  keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bpv));
                keyPair = new KeyPair(publicKey, privateKey);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            signCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            signCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        QuerySigner object = new QuerySigner();
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            SigningInterface stub = (SigningInterface) UnicastRemoteObject.exportObject(object, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("QuerySigner", stub);
            System.out.println("QuerySigner bound");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        apiport = Integer.parseInt(p.getProperty("apiport"));
        port(apiport);
        Spark.post("/sign/", (request, response) -> {
            String name = request.queryParams("name");
            String query = request.queryParams("query");
            byte[] res = object.signInstallQuery(query, name);
            if(res == null){
                System.out.println("REJECTED");
                return "";
            } else{
                byte[] base64sign = Base64.getEncoder().encode(res);
                System.out.println("ACCEPTED");
                System.out.println(name + ": " + query);
                System.out.println("sign: " + new String(base64sign));
                return base64sign;
            }
        });
    }

    synchronized public byte[] signInstallQuery(String query, String name) throws RemoteException {
        QueryInfo q = new QueryInfo(name, query);
        if(installedQueries.containsKey(name) && !installedQueries.get(name).equals(query)){
            return null;
        }
        Yylex lex = new Yylex(new ByteArrayInputStream(query.getBytes()));
        try {
            new parser(lex).pProgram();
        } catch (Exception e) {
            return null;
        }
        int idx = query.indexOf("AS");
        String query2 = query.substring(idx);
        query2.toLowerCase();
        if(query2.indexOf("contacts") != -1){
            return null;
        }
        if(query2.indexOf("name") != -1){
            return null;
        }
        byte[] sign = getSign(q);
        if(sign != null){
            installedQueries.put(name, query);
        }
        return sign;
    }
    String DIGEST_ALGORITHM = "SHA-1";
    public byte [] getSign(QueryInfo q){
        Kryo k = new Kryo();
        byte[] bytes = q.serialize();
        try {
            MessageDigest digestGenerator =
                    MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] digest = digestGenerator.digest(bytes);
            System.out.println(new String(Base64.getEncoder().encode(digest)));
            byte[] encrypted = signCipher.doFinal(digest);
            try {
                Cipher signCipher2 = Cipher.getInstance(ENCRYPTION_ALGORITHM);
                signCipher2.init(Cipher.DECRYPT_MODE, publicKey);
                byte[] decrypted = signCipher2.doFinal(encrypted);
                if(Arrays.equals(decrypted, digest)){
                    System.out.println("OKEDOKE");
                } else{
                    System.out.println("wut");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return encrypted;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }


    synchronized public byte[] signUninstallQuery(String name) throws RemoteException {
        return null;
    }
}
